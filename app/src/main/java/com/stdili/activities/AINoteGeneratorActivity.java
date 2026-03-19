package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.models.StudyNote;
import com.stdili.services.GeminiAIService;
import com.stdili.services.NotesService;

public class AINoteGeneratorActivity extends AppCompatActivity {
    private GeminiAIService geminiAIService;
    private NotesService notesService;
    private EditText etSubject, etTopic, etContent;
    private TextView tvGeneratedNotes;
    private Button btnGenerateNotes, btnSaveNotes;
    private String generatedContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_notes_generator);

        geminiAIService = new GeminiAIService();
        notesService = new NotesService();

        etSubject = findViewById(R.id.etSubject);
        etTopic = findViewById(R.id.etTopic);
        etContent = findViewById(R.id.etContent);
        tvGeneratedNotes = findViewById(R.id.tvGeneratedNotes);
        btnGenerateNotes = findViewById(R.id.btnGenerateNotes);
        btnSaveNotes = findViewById(R.id.btnSaveNotes);

        btnGenerateNotes.setOnClickListener(v -> generateNotes());
        btnSaveNotes.setOnClickListener(v -> saveNotes());
        
        setTitle("AI Notes Generator");
        Toast.makeText(this, "AI Notes Generator Ready", Toast.LENGTH_SHORT).show();
    }

    private void generateNotes() {
        String subject = etSubject.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (subject.isEmpty() || topic.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGenerateNotes.setEnabled(false);
        btnGenerateNotes.setText("Generating...");

        geminiAIService.generateStudyNotes(subject, topic, content, new GeminiAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                generatedContent = response;
                tvGeneratedNotes.setText(response);
                btnGenerateNotes.setEnabled(true);
                btnGenerateNotes.setText("✨ Generate Organized Notes");
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AINoteGeneratorActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                btnGenerateNotes.setEnabled(true);
                btnGenerateNotes.setText("✨ Generate Organized Notes");
            }
        });
    }

    private void saveNotes() {
        if (generatedContent == null || generatedContent.isEmpty()) {
            Toast.makeText(this, "Generate notes first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        StudyNote note = new StudyNote();
        note.setUserId(userId);
        note.setSubject(etSubject.getText().toString().trim());
        note.setTopic(etTopic.getText().toString().trim());
        note.setTitle("AI Notes: " + note.getTopic());
        note.setContent(generatedContent);
        note.setGeneratedBy("ai");
        note.setCreatedAt(System.currentTimeMillis());
        note.setUpdatedAt(System.currentTimeMillis());
        note.setRating(0);

        notesService.saveNote(note, new NotesService.OnNoteSavedListener() {
            @Override
            public void onNoteSaved(StudyNote savedNote) {
                Toast.makeText(AINoteGeneratorActivity.this, "Notes saved successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AINoteGeneratorActivity.this, "Error saving notes: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
