package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stdili.R;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONObject;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameInput;
    private EditText groupDescriptionInput;
    private Button categoryStudiesButton, categoryProjectsButton, categoryCareerButton, categoryGeneralButton;
    private Button createButton, cancelButton;
    private ApiClient apiClient;
    private String selectedCategory = "General";
    private String currentUserRole;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initializeViews();
        validateUserRole();
        setupCategoryButtons();
    }

    private void initializeViews() {
        groupNameInput = findViewById(R.id.etGroupName);
        groupDescriptionInput = findViewById(R.id.etGroupDescription);
        categoryStudiesButton = findViewById(R.id.btnCatStudies);
        categoryProjectsButton = findViewById(R.id.btnCatProjects);
        categoryCareerButton = findViewById(R.id.btnCatCareer);
        categoryGeneralButton = findViewById(R.id.btnCatMisc);
        createButton = findViewById(R.id.btnCreateGroup);
        cancelButton = findViewById(R.id.btnCancel);

        SecureSessionManager sessionManager = new SecureSessionManager(this);
        currentUserId = sessionManager.getUserId();
        apiClient = ApiClient.getInstance(sessionManager);

        setTitle("Create Community Group");
        createButton.setOnClickListener(v -> createGroup());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void validateUserRole() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        com.stdili.utils.FirebaseHelper.getUser(currentUserId, user -> {
            currentUserRole = user != null ? user.getRole() : "junior";
            if (!("teacher".equalsIgnoreCase(currentUserRole) || "senior".equalsIgnoreCase(currentUserRole))) {
                Toast.makeText(this, "Only teachers and seniors can create groups", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void setupCategoryButtons() {
        categoryStudiesButton.setOnClickListener(v -> selectCategory("Studies", categoryStudiesButton));
        categoryProjectsButton.setOnClickListener(v -> selectCategory("Projects", categoryProjectsButton));
        categoryCareerButton.setOnClickListener(v -> selectCategory("Career", categoryCareerButton));
        categoryGeneralButton.setOnClickListener(v -> selectCategory("General", categoryGeneralButton));

        // Set default selection
        selectCategory("General", categoryGeneralButton);
    }

    private void selectCategory(String category, Button selectedButton) {
        selectedCategory = category;

        // Update button states
        categoryStudiesButton.setAlpha(0.5f);
        categoryProjectsButton.setAlpha(0.5f);
        categoryCareerButton.setAlpha(0.5f);
        categoryGeneralButton.setAlpha(0.5f);

        selectedButton.setAlpha(1.0f);
    }

    private void createGroup() {
        String groupName = groupNameInput.getText().toString().trim();
        String description = groupDescriptionInput.getText().toString().trim();

        if (groupName.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        createButton.setEnabled(false);
        createButton.setText("Creating...");
        JSONObject body = new JSONObject();
        try {
            body.put("name", groupName);
            body.put("description", description);
            body.put("category", selectedCategory);
        } catch (Exception ignored) {
        }
        apiClient.post("/api/groups", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    Toast.makeText(CreateGroupActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(CreateGroupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    createButton.setEnabled(true);
                    createButton.setText("Create Group");
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
