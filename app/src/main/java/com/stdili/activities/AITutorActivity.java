package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.stdili.R;
import com.stdili.services.LocalAIService;

public class AITutorActivity extends AppCompatActivity {

    private EditText etSubject, etTopic, etQuestion;
    private Button btnAsk;
    private TextView tvAnswer;

    private final LocalAIService localAIService = new LocalAIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_tutor);

        etSubject = findViewById(R.id.etSubject);
        etTopic = findViewById(R.id.etTopic);
        etQuestion = findViewById(R.id.etQuestion);
        btnAsk = findViewById(R.id.btnAsk);
        tvAnswer = findViewById(R.id.tvAnswer);

        setTitle("AI Tutor");

        btnAsk.setOnClickListener(v -> askTutor());
    }

    private void askTutor() {
        String subject = etSubject.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();

        if (subject.isEmpty() || topic.isEmpty() || question.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAsk.setEnabled(false);
        tvAnswer.setText("Thinking...");

        localAIService.tutorReply(subject, topic, question, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                btnAsk.setEnabled(true);
                tvAnswer.setText(response);
            }

            @Override
            public void onFailure(String error) {
                btnAsk.setEnabled(true);
                tvAnswer.setText(error);
            }
        });
    }
}

