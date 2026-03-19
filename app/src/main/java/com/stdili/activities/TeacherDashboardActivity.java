package com.stdili.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.StudentAdapter;
import com.stdili.models.Student;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity {

    private RecyclerView rvStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        rvStudents = findViewById(R.id.rvStudents);

        List<Student> students = new ArrayList<>();
        // Load students from Firebase

        StudentAdapter adapter = new StudentAdapter(students);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);
    }
}