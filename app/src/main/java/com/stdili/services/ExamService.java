package com.stdili.services;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stdili.models.ExamSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for exam conduction, proctoring, and monitoring with camera access
 */
public class ExamService {
    private static final String TAG = "ExamService";
    private FirebaseFirestore db;

    public ExamService() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new exam session with camera monitoring
     */
    public void createExamSession(ExamSession exam, OnExamCreatedListener listener) {
        try {
            db.collection("exams")
                    .add(exam)
                    .addOnSuccessListener(documentReference -> {
                        exam.setExamId(documentReference.getId());
                        // Update with the generated ID
                        documentReference.update("examId", documentReference.getId());
                        Log.d(TAG, "Exam session created: " + documentReference.getId());
                        listener.onExamCreated(exam);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating exam session", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating exam", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Start exam session and begin camera monitoring
     */
    public void startExamSession(String examId, String studentId, OnExamStartedListener listener) {
        try {
            // Update exam status to ongoing
            db.collection("exams")
                    .document(examId)
                    .update("status", "ongoing", "startTime", System.currentTimeMillis())
                    .addOnSuccessListener(aVoid -> {
                        // Create a monitoring session
                        createMonitoringSession(examId, studentId);
                        Log.d(TAG, "Exam session started: " + examId);
                        listener.onExamStarted(examId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error starting exam", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while starting exam", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Create a monitoring session for camera proctoring
     */
    private void createMonitoringSession(String examId, String studentId) {
        try {
            long timestamp = System.currentTimeMillis();
            
            db.collection("exams")
                    .document(examId)
                    .collection("monitoring")
                    .document(studentId)
                    .set(new java.util.HashMap<String, Object>() {{
                        put("studentId", studentId);
                        put("startTime", timestamp);
                        put("cameraActive", true);
                        put("suspiciousActivity", false);
                        put("flagCount", 0);
                        put("lastActivity", timestamp);
                    }})
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Monitoring session created for student: " + studentId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating monitoring session", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating monitoring session", e);
        }
    }

    /**
     * Record suspicious activity during exam (detected through camera)
     */
    public void recordSuspiciousActivity(String examId, String studentId, String activityType, 
                                        String description, OnUpdateListener listener) {
        try {
            db.collection("exams")
                    .document(examId)
                    .collection("monitoring")
                    .document(studentId)
                    .collection("incidents")
                    .add(new java.util.HashMap<String, Object>() {{
                        put("examId", examId);
                        put("studentId", studentId);
                        put("type", activityType); // "none_in_frame", "multiple_faces", "tab_switch", "phone_detected"
                        put("description", description);
                        put("timestamp", System.currentTimeMillis());
                        put("severity", calculateSeverity(activityType));
                    }})
                    .addOnSuccessListener(documentReference -> {
                        // Increment flag count
                        incrementFlagCount(examId, studentId);
                        Log.d(TAG, "Suspicious activity logged: " + activityType);
                        listener.onUpdateSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error recording suspicious activity", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while recording suspicious activity", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * End exam session
     */
    public void endExamSession(String examId, String studentId, int score, OnExamEndedListener listener) {
        try {
            db.collection("exams")
                    .document(examId)
                    .update("status", "completed", "endTime", System.currentTimeMillis())
                    .addOnSuccessListener(aVoid -> {
                        // Save exam result
                        saveExamResult(examId, studentId, score);
                        Log.d(TAG, "Exam session ended: " + examId);
                        listener.onExamEnded(examId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error ending exam", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while ending exam", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Save exam result with monitoring report
     */
    private void saveExamResult(String examId, String studentId, int score) {
        try {
            db.collection("exams")
                    .document(examId)
                    .collection("results")
                    .document(studentId)
                    .set(new java.util.HashMap<String, Object>() {{
                        put("studentId", studentId);
                        put("score", score);
                        put("timestamp", System.currentTimeMillis());
                        put("completedAt", System.currentTimeMillis());
                    }})
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving exam result", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving exam result", e);
        }
    }

    /**
     * Get all exams created by a teacher
     */
    public void getTeacherExams(String teacherId, OnExamsLoadedListener listener) {
        try {
            db.collection("exams")
                    .whereEqualTo("createdBy", teacherId)
                    .orderBy("startTime")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<ExamSession> exams = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            ExamSession exam = doc.toObject(ExamSession.class);
                            exam.setExamId(doc.getId());
                            exams.add(exam);
                        });
                        listener.onExamsLoaded(exams);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading exams", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading exams", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Get monitoring report for an exam
     */
    public void getMonitoringReport(String examId, String studentId, OnReportLoadedListener listener) {
        try {
            db.collection("exams")
                    .document(examId)
                    .collection("monitoring")
                    .document(studentId)
                    .collection("incidents")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<java.util.Map<String, Object>> incidents = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            incidents.add(doc.getData());
                        });
                        listener.onReportLoaded(incidents);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading monitoring report", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading monitoring report", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Helper method to calculate threat severity
     */
    private String calculateSeverity(String activityType) {
        switch (activityType) {
            case "none_in_frame":
            case "multiple_faces":
                return "high";
            case "tab_switch":
            case "phone_detected":
                return "medium";
            default:
                return "low";
        }
    }

    /**
     * Increment suspicious activity flag count
     */
    private void incrementFlagCount(String examId, String studentId) {
        db.collection("exams")
                .document(examId)
                .collection("monitoring")
                .document(studentId)
                .update("flagCount", com.google.firebase.firestore.FieldValue.increment(1))
                .addOnFailureListener(e -> Log.e(TAG, "Error incrementing flag count", e));
    }

    // Listener Interfaces
    public interface OnExamCreatedListener {
        void onExamCreated(ExamSession exam);
        void onError(String errorMessage);
    }

    public interface OnExamStartedListener {
        void onExamStarted(String examId);
        void onError(String errorMessage);
    }

    public interface OnExamEndedListener {
        void onExamEnded(String examId);
        void onError(String errorMessage);
    }

    public interface OnExamsLoadedListener {
        void onExamsLoaded(List<ExamSession> exams);
        void onError(String errorMessage);
    }

    public interface OnReportLoadedListener {
        void onReportLoaded(List<java.util.Map<String, Object>> incidents);
        void onError(String errorMessage);
    }

    public interface OnUpdateListener {
        void onUpdateSuccess();
        void onError(String errorMessage);
    }
}
