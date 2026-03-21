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
import com.stdili.utils.FirebaseHelper;
import com.stdili.R;
import com.stdili.activities.EditProfileActivity;
import com.stdili.activities.SettingsActivity;
import com.stdili.activities.WelcomeActivity;
import com.stdili.utils.LevelSystem;

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
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            tvName.setText("User");
            tvLevel.setText("Level 1");
            tvCoins.setText("0 Coins");
            return;
        }

        FirebaseHelper.getUser(uid, user -> {
            if (user == null) {
                tvName.setText("User");
                tvLevel.setText("Level 1");
                tvCoins.setText("0 Coins");
                return;
            }

            String name = user.getName();
            if (name == null || name.trim().isEmpty()) {
                name = user.getEmail();
            }
            if (name == null || name.trim().isEmpty()) {
                name = "User";
            }

            int level = user.getLevel();
            // Backfill if level was not saved yet
            if (level <= 0) {
                level = LevelSystem.calculateLevel(user.getXp());
            }

            tvName.setText(name);
            tvLevel.setText("Level " + level);
            tvCoins.setText(user.getCoins() + " Coins");
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), WelcomeActivity.class));
        getActivity().finish();
    }
}