package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stdili.R;
import com.stdili.models.User;
import com.stdili.utils.FirebaseHelper;
import com.stdili.utils.SecureSessionManager;
import java.util.concurrent.TimeUnit;

public class OtpLoginActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnSendOtp;
    private FirebaseAuth mAuth;
    private SecureSessionManager sessionManager;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SecureSessionManager(this);

        etPhone = findViewById(R.id.etPhone);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setLoading(false);
                Toast.makeText(OtpLoginActivity.this, "OTP verification failed. Check number format and retry.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                
                Intent intent = new Intent(OtpLoginActivity.this, OtpVerificationActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("phone", etPhone.getText().toString().trim());
                startActivity(intent);
                setLoading(false);
            }
        };

        btnSendOtp.setOnClickListener(v -> sendOtp());
    }

    private void sendOtp() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.startsWith("+") || phone.length() < 10) {
            Toast.makeText(this, "Use international format, e.g. +919876543210", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                        if (uid == null) {
                            Toast.makeText(this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sessionManager.saveSession("firebase_session", uid);
                        FirebaseHelper.getUser(uid, user -> {
                            if (user == null) {
                                // Create minimal profile for mobile login path
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
                                startActivity(new Intent(OtpLoginActivity.this, MainActivity.class));
                                finish();
                            } else if ("senior".equals(user.getRole()) || "teacher".equals(user.getRole())) {
                                startActivity(new Intent(OtpLoginActivity.this, TeacherDashboardActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(OtpLoginActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(this, "OTP sign-in failed. Please retry.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnSendOtp.setEnabled(!loading);
        btnSendOtp.setText(loading ? "Sending OTP..." : "Send OTP");
    }
}