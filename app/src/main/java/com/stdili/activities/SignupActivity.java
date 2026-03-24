package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.stdili.R;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etClassGrade, etSubjects, etGoals, etPreferredLanguage;
    private Spinner spRole;
    private Button btnSignup;
    private TextView tvLoginLink;
    private SecureSessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SecureSessionManager(this);
        apiClient = ApiClient.getInstance(sessionManager);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etClassGrade = findViewById(R.id.etClassGrade);
        etSubjects = findViewById(R.id.etSubjects);
        etGoals = findViewById(R.id.etGoals);
        etPreferredLanguage = findViewById(R.id.etPreferredLanguage);
        spRole = findViewById(R.id.spRole);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnSignup.setOnClickListener(v -> signupUser());
        tvLoginLink.setOnClickListener(v -> finish());
    }

    private void signupUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String classGrade = etClassGrade.getText().toString().trim();
        String subjectsStr = etSubjects.getText().toString().trim();
        String goals = etGoals.getText().toString().trim();
        String preferredLanguage = etPreferredLanguage.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String selectedRoleText = spRole.getSelectedItem().toString();
        String role;
        if (selectedRoleText.contains("Junior")) {
            role = "junior";
        } else if (selectedRoleText.contains("Senior")) {
            role = "senior";
        } else {
            role = "guest";
        }

        if (name.isEmpty() || email.isEmpty() || classGrade.isEmpty() || subjectsStr.isEmpty() || goals.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("guest".equals(role)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("IS_GUEST", true);
            startActivity(intent);
            finish();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> subjects = Arrays.asList(subjectsStr.split(","));
        JSONObject payload = new JSONObject();
        try {
            payload.put("name", name);
            payload.put("email", email);
            payload.put("password", password);
            payload.put("role", role);
            payload.put("classGrade", classGrade);
            payload.put("goals", goals);
            payload.put("preferredLanguage", preferredLanguage.isEmpty() ? "English" : preferredLanguage);
            payload.put("availability", "online");
            JSONArray subjectsArray = new JSONArray();
            for (String s : subjects) {
                subjectsArray.put(s.trim());
            }
            payload.put("subjects", subjectsArray);
        } catch (Exception ignored) {
        }
        setLoading(true);
        apiClient.post("/api/auth/signup", payload, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    JSONObject user = data.optJSONObject("user");
                    String token = data.optString("token", null);
                    String userId = user != null ? user.optString("_id", null) : null;
                    if (token == null || userId == null) {
                        Toast.makeText(SignupActivity.this, "Signup response missing session data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sessionManager.saveSession(token, userId);
                    Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        btnSignup.setEnabled(!loading);
        btnSignup.setText(loading ? "Creating account..." : "Create Account");
    }
}