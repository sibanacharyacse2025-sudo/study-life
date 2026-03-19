package com.stdili.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.stdili.R;
import com.stdili.services.CameraMonitoringService;
import com.stdili.services.ExamService;
import com.stdili.services.ExamTimerService;

import java.util.concurrent.ExecutionException;

public class ExamConductorActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final String TAG = "ExamConductor";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private TextureView cameraTextureView;
    private TextView timerText;
    private TextView monitoringStatusText;
    private TextView warningText;
    private Button startExamButton;
    private Button endExamButton;

    private ExamService examService;
    private ExamTimerService timerService;
    private CameraMonitoringService cameraService;
    private FaceDetector faceDetector;

    private String examId;
    private String studentId;
    private boolean isExamRunning = false;
    private int suspiciousActivityCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_conductor);

        examId = getIntent().getStringExtra("examId");
        studentId = getIntent().getStringExtra("studentId");

        initializeViews();
        setupServices();
        requestCameraPermission();
        initializeFaceDetection();
    }

    private void initializeViews() {
        cameraTextureView = findViewById(R.id.cameraTextureView);
        timerText = findViewById(R.id.timerText);
        monitoringStatusText = findViewById(R.id.monitoringStatusText);
        warningText = findViewById(R.id.warningText);
        startExamButton = findViewById(R.id.startExamButton);
        endExamButton = findViewById(R.id.endExamButton);

        cameraTextureView.setSurfaceTextureListener(this);

        startExamButton.setOnClickListener(v -> startExam());
        endExamButton.setOnClickListener(v -> endExam());
        endExamButton.setEnabled(false);

        setTitle("Exam Proctoring");
    }

    private void setupServices() {
        examService = new ExamService();
        timerService = new ExamTimerService();
        cameraService = new CameraMonitoringService(this);
    }

    private void initializeFaceDetection() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();
        faceDetector = FaceDetection.getClient(options);
        monitoringStatusText.setText("Ready to monitor");
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void startExam() {
        if (examId == null || studentId == null) {
            Toast.makeText(this, "Error: Missing exam or student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        isExamRunning = true;
        startExamButton.setEnabled(false);
        endExamButton.setEnabled(true);
        suspiciousActivityCount = 0;
        warningText.setText("");

        // Start 3-hour timer
        timerService.initializeTimer(180); // 180 minutes
        timerService.setOnTimerTickListener(new ExamTimerService.OnTimerTickListener() {
            @Override
            public void onTick(long timeRemaining) {
                updateTimerDisplay(timeRemaining);
            }

            @Override
            public void onTimeWarning(long timeRemaining) {
                warningText.setTextColor(Color.RED);
                warningText.setText("⏰ Time running out: " + formatTime(timeRemaining));
            }

            @Override
            public void onTimeExpired() {
                endExam();
            }
        });

        timerService.startTimer();
        monitoringStatusText.setText("🔴 Monitoring active...");

        // Initialize camera for monitoring
        if (cameraTextureView.isAvailable()) {
            setupCamera();
        }

        // Start exam session in Firebase
        examService.startExamSession(examId, studentId, new ExamService.OnExamStartedListener() {
            @Override
            public void onExamStarted(String startedExamId) {
                Toast.makeText(ExamConductorActivity.this, "Exam started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ExamConductorActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void endExam() {
        isExamRunning = false;
        startExamButton.setEnabled(true);
        endExamButton.setEnabled(false);

        if (timerService != null) {
            timerService.stopTimer();
        }

        monitoringStatusText.setText("✅ Monitoring stopped");
        warningText.setText("Exam completed. Submitting...");

        // End exam session
        examService.endExamSession(examId, studentId, 0, new ExamService.OnExamEndedListener() {
            @Override
            public void onExamEnded(String endedExamId) {
                Toast.makeText(ExamConductorActivity.this, "Exam submitted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ExamConductorActivity.this, "Error submitting: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCamera() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            return;
        }

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                initializePreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error initializing camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void initializePreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        androidx.camera.core.CameraSelector cameraSelector =
                new androidx.camera.core.CameraSelector.Builder()
                        .requireLensFacing(androidx.camera.core.CameraSelector.LENS_FACING_FRONT)
                        .build();

        try {
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            preview.setSurfaceProvider(null);
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera", e);
        }
    }

    private void updateTimerDisplay(long timeRemaining) {
        String displayText = formatTime(timeRemaining);
        timerText.setText(displayText);

        // Change color to red if less than 10% time remaining
        if (timeRemaining < (180 * 0.1)) { // 10% of 180 minutes = 18 minutes
            timerText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            timerText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private String formatTime(long minutes) {
        long hours = minutes / 60;
        long mins = minutes % 60;
        return String.format("%02d:%02d:%02d", hours, mins, 0);
    }

    private void onSuspiciousActivityDetected(String activityType) {
        suspiciousActivityCount++;
        warningText.setText("⚠️ Suspicious activity detected: " + activityType);
        warningText.setTextColor(Color.RED);

        examService.recordSuspiciousActivity(examId, studentId, activityType,
                "Activity detected at " + System.currentTimeMillis(),
                new ExamService.OnUpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        Log.d(TAG, "Suspicious activity recorded: " + activityType);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error recording activity: " + errorMessage);
                    }
                });

        if (suspiciousActivityCount >= 3) {
            Toast.makeText(this, "Multiple suspicious activities detected. Exam may be flagged.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(android.graphics.SurfaceTexture surface, int width, int height) {
        if (isExamRunning) {
            detectFacesInFrame();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(android.graphics.SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(android.graphics.SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(android.graphics.SurfaceTexture surface) {
        if (isExamRunning) {
            detectFacesInFrame();
        }
    }

    private void detectFacesInFrame() {
        // ML Kit face detection would be implemented here
        // For now, this is a placeholder for the detection logic
        Log.d(TAG, "Frame available for analysis");
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isExamRunning) {
            endExam();
        }
        if (timerService != null) {
            timerService.stopTimer();
        }
        if (faceDetector != null) {
            faceDetector.close();
        }
    }
}
