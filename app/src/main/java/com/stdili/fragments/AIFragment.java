package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.stdili.R;
import com.stdili.activities.AICounsellorActivity;
import com.stdili.activities.AITutorActivity;
import com.stdili.fragments.AIChatFragment;

public class AIFragment extends Fragment {

    private Button btnAICounsellor, btnAITutor, btnAIStudyAssistant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        btnAICounsellor = view.findViewById(R.id.btnAICounsellor);
        btnAITutor = view.findViewById(R.id.btnAITutor);
        btnAIStudyAssistant = view.findViewById(R.id.btnAIStudyAssistant);

        btnAICounsellor.setOnClickListener(v -> startActivity(new Intent(getContext(), AICounsellorActivity.class)));
        btnAITutor.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AITutorActivity.class));
        });
        btnAIStudyAssistant.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AIChatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}