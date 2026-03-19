package com.stdili.services;

import android.os.CountDownTimer;
import android.util.Log;

public class ExamTimerService {
    private static final String TAG = "ExamTimerService";
    private CountDownTimer countDownTimer;
    private long timeRemaining;

    public interface OnTimerTickListener {
        void onTick(long minutesRemaining);
        void onTimeWarning(long minutesRemaining);
        void onTimeExpired();
    }

    private OnTimerTickListener timerTickListener;

    public void initializeTimer(long durationMinutes) {
        this.timeRemaining = durationMinutes * 60 * 1000;
        Log.d(TAG, "Timer initialized for " + durationMinutes + " minutes");
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                if (timerTickListener != null) {
                    long minutesRemaining = millisUntilFinished / 1000 / 60;
                    timerTickListener.onTick(minutesRemaining);
                    if (minutesRemaining <= 15) {
                        timerTickListener.onTimeWarning(minutesRemaining);
                    }
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Timer finished");
                if (timerTickListener != null) {
                    timerTickListener.onTimeExpired();
                }
            }
        };
        countDownTimer.start();
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resumeTimer() {
        startTimer();
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setOnTimerTickListener(OnTimerTickListener listener) {
        this.timerTickListener = listener;
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Backwards-compatible stop method if used elsewhere
    public void stop() {
        stopTimer();
    }
}
