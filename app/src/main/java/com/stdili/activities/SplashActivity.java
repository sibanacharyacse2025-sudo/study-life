package com.stdili.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;

public class SplashActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestNotificationPermissionIfNeeded();

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(OnboardingActivity.PREFS_NAME, MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean(OnboardingActivity.KEY_ONBOARDING_DONE, false);

            if (!onboardingDone) {
                startActivity(new Intent(this, OnboardingActivity.class));
                finish();
                return;
            }

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, WelcomeActivity.class));
            }
            finish();
        }, 1500);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 33) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                NOTIFICATION_PERMISSION_REQUEST
        );
    }
}