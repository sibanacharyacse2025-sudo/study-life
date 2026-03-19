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
import com.stdili.services.ChatService;

import java.util.ArrayList;
import java.util.List;

public class CommunityGroupActivity extends AppCompatActivity {

    private RecyclerView groupRecyclerView;
    private Button createGroupButton;
    private Button categoryAllButton, categoryStudiesButton, categoryProjectsButton, categoryCareerButton;
    private CommunityGroupAdapter groupAdapter;
    private List<CommunityGroup> allGroups, filteredGroups;
    private ChatService chatService;
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

        chatService = new ChatService();
        currentUserId = "currentUserId"; // Replace with actual current user ID

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
        // Fetch user role from Firebase/Service
        // For now, mock data
        currentUserRole = "student"; // Replace with actual role fetch

        // Check if user can create groups
        boolean canCreateGroup = "teacher".equalsIgnoreCase(currentUserRole) || 
                                 "senior".equalsIgnoreCase(currentUserRole);
        createGroupButton.setEnabled(canCreateGroup);
        if (!canCreateGroup) {
            createGroupButton.setAlpha(0.5f);
        }

        loadGroups();
    }

    private void loadGroups() {
        chatService.getCommunityGroups(new ChatService.OnGroupsLoadedListener() {
            @Override
            public void onGroupsLoaded(List<CommunityGroup> groups) {
                allGroups.clear();
                allGroups.addAll(groups);
                filterGroups(currentCategory); // Apply current filter
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommunityGroupActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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
        if (!group.getMembers().contains(currentUserId)) {
            joinGroup(group);
        } else {
            navigateToGroupChat(group);
        }
    }

    private void joinGroup(CommunityGroup group) {
        group.getMembers().add(currentUserId);
        group.setMemberCount(group.getMembers().size());

        chatService.updateGroup(group, new ChatService.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CommunityGroupActivity.this, "Joined group!", Toast.LENGTH_SHORT).show();
                navigateToGroupChat(group);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommunityGroupActivity.this, "Error joining: " + error, Toast.LENGTH_SHORT).show();
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
        if (chatService != null) {
            chatService.cleanup();
        }
    }
}
