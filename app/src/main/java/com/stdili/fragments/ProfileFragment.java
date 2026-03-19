package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.activities.EditProfileActivity;
import com.stdili.activities.SettingsActivity;
import com.stdili.activities.WelcomeActivity;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvLevel, tvCoins;
    private Button btnEditProfile, btnSettings, btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvCoins = view.findViewById(R.id.tvCoins);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load user data
        loadUserData();

        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserData() {
        // Load from Firebase
        tvName.setText("John Doe");
        tvLevel.setText("Level 5");
        tvCoins.setText("1250 Coins");
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), WelcomeActivity.class));
        getActivity().finish();
    }
}