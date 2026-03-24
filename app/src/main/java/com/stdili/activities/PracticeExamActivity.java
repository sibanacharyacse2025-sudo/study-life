package com.stdili.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.stdili.R;
import com.stdili.services.AdeonAIService;

public class PracticeExamActivity extends AppCompatActivity {

    private EditText etSubject, etTopic;
    private Button btnGenerate;
    private TextView tvQuestions;
    private LinearLayout llInput;
    private AdeonAIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_exam);

        etSubject = findViewById(R.id.etSubject);
        etTopic = findViewById(R.id.etTopic);
        btnGenerate = findViewById(R.id.btnGenerate);
        tvQuestions = findViewById(R.id.tvQuestions);
        llInput = findViewById(R.id.llInput);

        aiService = new AdeonAIService();

        btnGenerate.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String topic = etTopic.getText().toString().trim();

            if (subject.isEmpty() || topic.isEmpty()) {
                Toast.makeText(this, "Please enter subject and topic", Toast.LENGTH_SHORT).show();
                return;
            }

            tvQuestions.setText("Generating MCQs using Ollama...");
            llInput.setVisibility(View.GONE);

            aiService.generatePracticeExam(subject, topic, "Medium", new AdeonAIService.OnResponse() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> tvQuestions.setText(response));
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(PracticeExamActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        llInput.setVisibility(View.VISIBLE);
                        tvQuestions.setText("");
                    });
                }
            });
        });
    }
}