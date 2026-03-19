package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.activities.GroupChatActivity;
import com.stdili.adapters.GroupAdapter;
import com.stdili.models.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment implements GroupAdapter.OnGroupClickListener {

    private RecyclerView rvGroups;
    private Button btnCreateGroup;
    private GroupAdapter adapter;
    private List<Group> groups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        rvGroups = view.findViewById(R.id.rvGroups);
        btnCreateGroup = view.findViewById(R.id.btnCreateGroup);

        groups = new ArrayList<>();
        loadGroups();

        adapter = new GroupAdapter(groups, this);
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGroups.setAdapter(adapter);

        btnCreateGroup.setOnClickListener(v -> createNewGroup());

        return view;
    }

    private void loadGroups() {
        // Load from Firebase
        List<String> members1 = new ArrayList<>();
        members1.add("user1");
        members1.add("user2");
        groups.add(new Group("Math Study Group", "Advanced Calculus preparation", members1));

        List<String> members2 = new ArrayList<>();
        members2.add("user3");
        members2.add("user4");
        groups.add(new Group("Physics Enthusiasts", "Quantum mechanics discussion", members2));
    }

    private void createNewGroup() {
        // Open create group dialog or activity
    }

    @Override
    public void onGroupClick(Group group) {
        Intent intent = new Intent(getContext(), GroupChatActivity.class);
        intent.putExtra("group_id", group.getId());
        startActivity(intent);
    }

    @Override
    public void onJoinClick(Group group) {
        // Join group logic
    }
}