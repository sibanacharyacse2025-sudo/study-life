package com.stdili.activities;

import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.stdili.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch swNotifications, swPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swNotifications = findViewById(R.id.swNotifications);
        swPrivacy = findViewById(R.id.swPrivacy);

        // Load settings
    }
}