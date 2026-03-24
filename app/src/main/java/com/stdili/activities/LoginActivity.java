package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.stdili.R;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnGuestLogin;
    private TextView tvForgotPassword, tvOtpLogin, tvSignUp;
    private SecureSessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SecureSessionManager(this);
        apiClient = ApiClient.getInstance(sessionManager);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuestLogin = findViewById(R.id.btnGuestLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvOtpLogin = findViewById(R.id.tvOtpLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(v -> loginUser());
        btnGuestLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("IS_GUEST", true);
            startActivity(intent);
            finish();
        });
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvOtpLogin.setOnClickListener(v -> startActivity(new Intent(this, OtpLoginActivity.class)));
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        JSONObject payload = new JSONObject();
        try {
            payload.put("email", email);
            payload.put("password", password);
        } catch (Exception ignored) {
        }
        apiClient.post("/api/auth/login", payload, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    JSONObject user = data.optJSONObject("user");
                    String token = data.optString("token", null);
                    String userId = user != null ? user.optString("_id", null) : null;
                    String role = user != null ? user.optString("role", "junior") : "junior";
                    if (token == null || userId == null) {
                        Toast.makeText(LoginActivity.this, "Login response missing session data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sessionManager.saveSession(token, userId);
                    if ("senior".equals(role) || "teacher".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, TeacherDashboardActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnGuestLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Logging in..." : getString(R.string.login));
    }
}