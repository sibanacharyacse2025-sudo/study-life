package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stdili.R;
import com.stdili.models.User;
import com.stdili.utils.FirebaseHelper;
import com.stdili.utils.SecureSessionManager;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private TextView tvPhoneHint;
    private String verificationId;
    private String phone;
    private SecureSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        verificationId = getIntent().getStringExtra("verificationId");
        phone = getIntent().getStringExtra("phone");
        sessionManager = new SecureSessionManager(this);

        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvPhoneHint = findViewById(R.id.tvPhoneHint);
        tvPhoneHint.setText(phone == null ? "Enter code received on your phone" : "Code sent to " + phone);

        btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (verificationId == null || verificationId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid verification session. Retry login.", Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid == null) {
                            Toast.makeText(this, "Sign-in succeeded but session missing.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sessionManager.saveSession("firebase_session", uid);
                        FirebaseHelper.getUser(uid, user -> {
                            if (user == null) {
                                User newUser = new User();
                                newUser.setUid(uid);
                                newUser.setName("Student");
                                newUser.setEmail("");
                                newUser.setRole("junior");
                                newUser.setClassGrade("");
                                newUser.setGoals("");
                                newUser.setPreferredLanguage("English");
                                newUser.setLevel(1);
                                newUser.setXp(0);
                                newUser.setPoints(100);
                                newUser.setCoins(0);
                                newUser.setStudyHours(0);
                                newUser.setStreak(0);
                                FirebaseHelper.saveUser(newUser);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else if ("senior".equals(user.getRole()) || "teacher".equals(user.getRole())) {
                                startActivity(new Intent(this, TeacherDashboardActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(this, "OTP verification failed. Check code and retry.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnVerify.setEnabled(!loading);
        btnVerify.setText(loading ? "Verifying..." : "Verify OTP");
    }
}