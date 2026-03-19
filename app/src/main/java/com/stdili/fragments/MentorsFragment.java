package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.activities.ChatActivity;
import com.stdili.adapters.MentorAdapter;
import com.stdili.models.Mentor;
import java.util.ArrayList;
import java.util.List;

public class MentorsFragment extends Fragment implements MentorAdapter.OnMentorClickListener {

    private RecyclerView rvMentors;
    private MentorAdapter adapter;
    private List<Mentor> mentors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentors, container, false);

        rvMentors = view.findViewById(R.id.rvMentors);

        mentors = new ArrayList<>();
        loadMentors();

        adapter = new MentorAdapter(mentors, this);
        rvMentors.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMentors.setAdapter(adapter);

        return view;
    }

    private void loadMentors() {
        // Load from Firebase
        mentors.add(new Mentor("Dr. Sarah Johnson", "Mathematics", true));
        mentors.add(new Mentor("Prof. Michael Chen", "Physics", false));
        mentors.add(new Mentor("Ms. Emily Davis", "Chemistry", true));
        mentors.add(new Mentor("Mr. David Wilson", "Biology", true));
        mentors.add(new Mentor("Dr. Lisa Brown", "Computer Science", false));
    }

    @Override
    public void onMentorClick(Mentor mentor) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("mentor_name", mentor.getName());
        startActivity(intent);
    }
}