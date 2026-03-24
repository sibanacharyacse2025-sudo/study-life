package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.stdili.R;
import com.stdili.activities.CommunityGroupActivity;

public class GroupsFragment extends Fragment {

    private Button btnCreateGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        btnCreateGroup = view.findViewById(R.id.btnCreateGroup);

        btnCreateGroup.setOnClickListener(v -> openCommunityGroups());
        view.findViewById(R.id.rvGroups).setOnClickListener(v -> openCommunityGroups());

        return view;
    }

    private void openCommunityGroups() {
        startActivity(new Intent(getContext(), CommunityGroupActivity.class));
    }
}