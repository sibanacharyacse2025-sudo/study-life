package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stdili.R;
import com.stdili.adapters.CommunityGroupAdapter;
import com.stdili.models.CommunityGroup;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunityGroupActivity extends AppCompatActivity {

    private RecyclerView groupRecyclerView;
    private Button createGroupButton;
    private Button categoryAllButton, categoryStudiesButton, categoryProjectsButton, categoryCareerButton;
    private CommunityGroupAdapter groupAdapter;
    private List<CommunityGroup> allGroups, filteredGroups;
    private ApiClient apiClient;
    private String currentUserRole;
    private String currentUserId;
    private String currentCategory = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_groups);

        initializeViews();
        setupRecyclerView();
        setupCategoryFilters();
        fetchUserRoleAndLoadGroups();
    }

    private void initializeViews() {
        groupRecyclerView = findViewById(R.id.rvCommunityGroups);
        createGroupButton = findViewById(R.id.btnCreateGroup);
        categoryAllButton = findViewById(R.id.btnAllGroups);
        categoryStudiesButton = findViewById(R.id.btnStudies);
        categoryProjectsButton = findViewById(R.id.btnProjects);
        categoryCareerButton = findViewById(R.id.btnCareer);

        SecureSessionManager sessionManager = new SecureSessionManager(this);
        apiClient = ApiClient.getInstance(sessionManager);
        currentUserId = sessionManager.getUserId();

        setTitle("Community Groups");

        createGroupButton.setOnClickListener(v -> openCreateGroupActivity());
    }

    private void setupRecyclerView() {
        allGroups = new ArrayList<>();
        filteredGroups = new ArrayList<>();
        groupAdapter = new CommunityGroupAdapter(filteredGroups, group -> openGroupChat(group));
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerView.setAdapter(groupAdapter);
    }

    private void setupCategoryFilters() {
        categoryAllButton.setOnClickListener(v -> filterGroups("all"));
        categoryStudiesButton.setOnClickListener(v -> filterGroups("Studies"));
        categoryProjectsButton.setOnClickListener(v -> filterGroups("Projects"));
        categoryCareerButton.setOnClickListener(v -> filterGroups("Career"));
    }

    private void fetchUserRoleAndLoadGroups() {
        if (currentUserId == null) {
            currentUserRole = "guest";
            createGroupButton.setEnabled(false);
            createGroupButton.setAlpha(0.5f);
            loadGroups();
            return;
        }

        com.stdili.utils.FirebaseHelper.getUser(currentUserId, user -> {
            currentUserRole = user != null ? user.getRole() : "junior";
            boolean canCreateGroup = "teacher".equalsIgnoreCase(currentUserRole) ||
                    "senior".equalsIgnoreCase(currentUserRole);
            createGroupButton.setEnabled(canCreateGroup);
            createGroupButton.setAlpha(canCreateGroup ? 1f : 0.5f);
            loadGroups();
        });
    }

    private void loadGroups() {
        apiClient.get("/api/groups", true, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    allGroups.clear();
                    JSONArray groups = data.optJSONArray("groups");
                    if (groups != null) {
                        for (int i = 0; i < groups.length(); i++) {
                            JSONObject g = groups.optJSONObject(i);
                            if (g == null) continue;
                            CommunityGroup group = new CommunityGroup();
                            group.setGroupId(g.optString("_id", ""));
                            group.setName(g.optString("name", ""));
                            group.setDescription(g.optString("description", ""));
                            group.setCategory(g.optString("category", "General"));
                            group.setCreatorId(g.optString("adminId", ""));
                            JSONArray membersJson = g.optJSONArray("members");
                            List<String> members = new ArrayList<>();
                            if (membersJson != null) {
                                for (int m = 0; m < membersJson.length(); m++) {
                                    members.add(membersJson.optString(m));
                                }
                            }
                            group.setMembers(members);
                            group.setMemberCount(members.size());
                            allGroups.add(group);
                        }
                    }
                    filterGroups(currentCategory);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(CommunityGroupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void filterGroups(String category) {
        currentCategory = category;
        filteredGroups.clear();

        if ("all".equalsIgnoreCase(category)) {
            filteredGroups.addAll(allGroups);
        } else {
            for (CommunityGroup group : allGroups) {
                if (category.equalsIgnoreCase(group.getCategory())) {
                    filteredGroups.add(group);
                }
            }
        }

        groupAdapter.notifyDataSetChanged();
    }

    private void openGroupChat(CommunityGroup group) {
        // Check if user is a member, if not, join first
        if (group.getMembers() == null) {
            group.setMembers(new ArrayList<>());
        }
        if (!group.getMembers().contains(currentUserId)) {
            joinGroup(group);
        } else {
            navigateToGroupChat(group);
        }
    }

    private void joinGroup(CommunityGroup group) {
        JSONObject body = new JSONObject();
        try {
            body.put("userId", currentUserId);
        } catch (Exception ignored) {
        }
        apiClient.post("/api/groups/" + group.getGroupId() + "/join", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    Toast.makeText(CommunityGroupActivity.this, "Joined group!", Toast.LENGTH_SHORT).show();
                    navigateToGroupChat(group);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(CommunityGroupActivity.this, "Error joining: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void navigateToGroupChat(CommunityGroup group) {
        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.putExtra("groupId", group.getGroupId());
        intent.putExtra("groupName", group.getName());
        startActivity(intent);
    }

    private void openCreateGroupActivity() {
        if (!("teacher".equalsIgnoreCase(currentUserRole) || "senior".equalsIgnoreCase(currentUserRole))) {
            Toast.makeText(this, "Only teachers and seniors can create groups", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CreateGroupActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadGroups(); // Refresh groups list
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
