package com.stdili.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.QuickActionAdapter;
import com.stdili.adapters.GettingStartedAdapter;
import com.stdili.adapters.BadgeAdapter;
import com.stdili.activities.AICounsellorActivity;
import com.stdili.activities.PomodoroActivity;
import com.stdili.activities.DailyGoalsActivity;
import com.stdili.models.QuickAction;
import com.stdili.models.GettingStartedItem;
import com.stdili.models.Badge;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements QuickActionAdapter.OnActionClickListener {

    private TextView tvGreeting, tvStudyHours, tvStreak, tvPoints, tvLevel;
    private RecyclerView rvQuickActions, rvGettingStarted, rvBadges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvStudyHours = view.findViewById(R.id.tvStudyHours);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvLevel = view.findViewById(R.id.tvLevel);
        rvQuickActions = view.findViewById(R.id.rvQuickActions);
        rvGettingStarted = view.findViewById(R.id.rvGettingStarted);
        rvBadges = view.findViewById(R.id.rvBadges);

        // Load user data
        loadUserData();

        // Setup quick actions
        setupQuickActions();

        // Setup getting started
        setupGettingStarted();

        // Setup badges
        setupBadges();

        return view;
    }

    private void loadUserData() {
        // Fetch from Firebase or local
        tvGreeting.setText(String.format(getString(R.string.good_evening), "User"));
        tvStudyHours.setText("10 hours");
        tvStreak.setText("5 days");
        tvPoints.setText("500");
        tvLevel.setText("Level 3");
    }

    private void setupQuickActions() {
        List<QuickAction> actions = new ArrayList<>();
        actions.add(new QuickAction("AI Tutor", R.drawable.ic_ai));
        actions.add(new QuickAction("Pomodoro", R.drawable.ic_study));
        actions.add(new QuickAction("Goals", R.drawable.ic_add));
        actions.add(new QuickAction("Voice Chat", R.drawable.ic_voice));
        actions.add(new QuickAction("Face Tutor", R.drawable.ic_face));
        actions.add(new QuickAction("Smart Notes", R.drawable.ic_notes));
        actions.add(new QuickAction("Practice", R.drawable.ic_practice));
        actions.add(new QuickAction("Mentors", R.drawable.ic_mentor));
        QuickActionAdapter adapter = new QuickActionAdapter(actions, this);
        rvQuickActions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvQuickActions.setAdapter(adapter);
    }

    @Override
    public void onActionClick(QuickAction action) {
        if (action.getTitle().equals("AI Tutor")) {
            startActivity(new Intent(getContext(), AICounsellorActivity.class));
        } else if (action.getTitle().equals("Pomodoro")) {
            startActivity(new Intent(getContext(), PomodoroActivity.class));
        } else if (action.getTitle().equals("Goals")) {
            startActivity(new Intent(getContext(), DailyGoalsActivity.class));
        }
    }

    private void setupGettingStarted() {
        List<GettingStartedItem> items = new ArrayList<>();
        items.add(new GettingStartedItem("Complete your profile", "Add more details to get personalized recommendations"));
        items.add(new GettingStartedItem("Join a study group", "Connect with peers for collaborative learning"));
        items.add(new GettingStartedItem("Take your first quiz", "Test your knowledge and earn points"));
        GettingStartedAdapter adapter = new GettingStartedAdapter(items);
        rvGettingStarted.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGettingStarted.setAdapter(adapter);
    }

    private void setupBadges() {
        List<Badge> badges = new ArrayList<>();
        badges.add(new Badge("First Login", "Welcome to Stdili!", R.drawable.ic_badge_first_login));
        badges.add(new Badge("Study Streak", "5 days in a row", R.drawable.ic_badge_streak));
        badges.add(new Badge("Top Scorer", "Achieved in Math quiz", R.drawable.ic_badge_top_scorer));
        BadgeAdapter adapter = new BadgeAdapter(badges);
        rvBadges.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvBadges.setAdapter(adapter);
    }
}