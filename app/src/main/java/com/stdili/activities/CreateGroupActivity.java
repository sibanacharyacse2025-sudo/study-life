package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stdili.R;
import com.stdili.models.CommunityGroup;
import com.stdili.services.ChatService;
import java.util.Date;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameInput;
    private EditText groupDescriptionInput;
    private Button categoryStudiesButton, categoryProjectsButton, categoryCareerButton, categoryGeneralButton;
    private Button createButton, cancelButton;
    private ChatService chatService;
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

        chatService = new ChatService();
        currentUserId = "currentUserId"; // Replace with actual current user ID
        currentUserRole = "teacher"; // Replace with actual role fetch

        setTitle("Create Community Group");
        createButton.setOnClickListener(v -> createGroup());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void validateUserRole() {
        if (!("teacher".equalsIgnoreCase(currentUserRole) || "senior".equalsIgnoreCase(currentUserRole))) {
            Toast.makeText(this, "Only teachers and seniors can create groups", Toast.LENGTH_LONG).show();
            finish();
        }
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

        CommunityGroup group = new CommunityGroup();
        group.setName(groupName);
        group.setDescription(description);
        group.setCategory(selectedCategory);
        group.setCreatorId(currentUserId);
        group.setCreatorRole(currentUserRole);
        group.setCreatedAt(new Date());
        group.setMemberCount(1);

        chatService.createCommunityGroup(group, new ChatService.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreateGroupActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateGroupActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                createButton.setEnabled(true);
                createButton.setText("Create Group");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
