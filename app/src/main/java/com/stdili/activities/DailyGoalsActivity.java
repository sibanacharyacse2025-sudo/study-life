package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.GoalAdapter;
import com.stdili.models.Goal;
import java.util.ArrayList;
import java.util.List;

public class DailyGoalsActivity extends AppCompatActivity {
    private EditText etGoal;
    private Button btnAddGoal;
    private RecyclerView rvGoals;
    private GoalAdapter adapter;
    private List<Goal> goals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        etGoal = findViewById(R.id.etGoal);
        btnAddGoal = findViewById(R.id.btnAddGoal);
        rvGoals = findViewById(R.id.rvGoals);

        goals = new ArrayList<>();
        adapter = new GoalAdapter(goals);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(adapter);

        btnAddGoal.setOnClickListener(v -> {
            String title = etGoal.getText().toString().trim();
            if (!title.isEmpty()) {
                goals.add(new Goal(title));
                adapter.notifyItemInserted(goals.size() - 1);
                etGoal.setText("");
            }
        });
    }
}