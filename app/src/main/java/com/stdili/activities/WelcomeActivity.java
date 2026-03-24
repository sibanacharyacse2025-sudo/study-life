package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.utils.SecureSessionManager;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnStudent, btnTeacher, btnGuest;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SecureSessionManager sessionManager = new SecureSessionManager(this);
        boolean hasBackendSession = sessionManager.getAccessToken() != null && sessionManager.getUserId() != null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null || hasBackendSession) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        btnStudent = findViewById(R.id.btnStudent);
        btnTeacher = findViewById(R.id.btnTeacher);
        btnGuest = findViewById(R.id.btnGuest);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnStudent.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnTeacher.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        
        btnGuest.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("IS_GUEST", true);
            startActivity(intent);
            finish();
        });

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }
}