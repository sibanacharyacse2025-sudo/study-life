package com.stdili.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.stdili.R;
import java.util.Locale;

public class PomodoroActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 1500000; // 25 minutes
    private static final long MIN_TIME_IN_MILLIS = 60000; // 1 minute minimum
    private static final long MAX_TIME_IN_MILLIS = 3600000; // 60 minutes maximum

    private TextView tvTimer;
    private Button btnStartPause, btnReset, btnPlus, btnMinus;

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        tvTimer = findViewById(R.id.tvTimer);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);

        btnStartPause.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btnReset.setOnClickListener(v -> resetTimer());

        btnPlus.setOnClickListener(v -> addOneMinute());
        btnMinus.setOnClickListener(v -> subtractOneMinute());

        updateCountDownText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnStartPause.setText("Start");
                btnStartPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            }
        }.start();

        timerRunning = true;
        btnStartPause.setText("Pause");
        btnReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        btnStartPause.setText("Start");
        btnReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        btnReset.setVisibility(View.INVISIBLE);
        btnStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    private void addOneMinute() {
        if (!timerRunning) {
            long newTime = timeLeftInMillis + 60000; // Add 1 minute (60000 ms)
            if (newTime <= MAX_TIME_IN_MILLIS) {
                timeLeftInMillis = newTime;
                updateCountDownText();
            }
        }
    }

    private void subtractOneMinute() {
        if (!timerRunning) {
            long newTime = timeLeftInMillis - 60000; // Subtract 1 minute (60000 ms)
            if (newTime >= MIN_TIME_IN_MILLIS) {
                timeLeftInMillis = newTime;
                updateCountDownText();
            }
        }
    }
}