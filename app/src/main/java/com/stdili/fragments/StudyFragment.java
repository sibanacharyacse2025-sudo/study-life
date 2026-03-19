package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.stdili.R;
import com.stdili.activities.StudyHubActivity;
import com.stdili.activities.PomodoroActivity;

public class StudyFragment extends Fragment {

    private Button btnSmartNotes, btnPractice, btnPdfLibrary, btnFlashcards, btnStudyGroups, btnPomodoro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        btnSmartNotes = view.findViewById(R.id.btnSmartNotes);
        btnPractice = view.findViewById(R.id.btnPractice);
        btnPdfLibrary = view.findViewById(R.id.btnPdfLibrary);
        btnFlashcards = view.findViewById(R.id.btnFlashcards);
        btnStudyGroups = view.findViewById(R.id.btnStudyGroups);
        btnPomodoro = view.findViewById(R.id.btnPomodoro);

        btnSmartNotes.setOnClickListener(v -> startActivity(new Intent(getContext(), StudyHubActivity.class)));
        btnPractice.setOnClickListener(v -> {
            // Open practice activity
        });
        btnPdfLibrary.setOnClickListener(v -> {
            // Open PDF library
        });
        btnFlashcards.setOnClickListener(v -> {
            // Open flashcards
        });
        btnPomodoro.setOnClickListener(v -> startActivity(new Intent(getContext(), PomodoroActivity.class)));
        btnStudyGroups.setOnClickListener(v -> {
            // Open study groups (could navigate to network tab)
        });

        return view;
    }
}