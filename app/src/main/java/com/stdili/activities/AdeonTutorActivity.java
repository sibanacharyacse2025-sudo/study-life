package com.stdili.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.MessageAdapter;
import com.stdili.models.Message;
import com.stdili.services.AdeonAIService;
import java.util.ArrayList;
import java.util.List;

/**
 * Advanced AI Tutoring with Adeon
 * Specialized for learning any subject with step-by-step guidance
 */
public class AdeonTutorActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etQuestion;
    private EditText etSubject;
    private EditText etTopic;
    private Button btnGetHelp;
    private List<Message> messages;
    private MessageAdapter adapter;
    private AdeonAIService adeonAIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adeon_tutor);

        // Initialize views
        rvMessages = findViewById(R.id.rvMessages);
        etQuestion = findViewById(R.id.etQuestion);
        etSubject = findViewById(R.id.etSubject);
        etTopic = findViewById(R.id.etTopic);
        btnGetHelp = findViewById(R.id.btnGetHelp);

        // Initialize messages
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        // Initialize Adeon
        adeonAIService = new AdeonAIService();

        // Add welcome message
        String welcome = "🎓 **Welcome to Adeon Tutor!** 🎓\n\n"
                + "I'm Adeon, your expert tutor specializing in:\n"
                + "📚 Mathematics, Science, History, Languages\n"
                + "💡 Problem-solving with step-by-step explanations\n"
                + "🎯 Concept mastery through interactive learning\n\n"
                + "**How to use:**\n"
                + "1️⃣ Enter your subject (e.g., Physics, Biology)\n"
                + "2️⃣ Enter the topic (e.g., Photosynthesis)\n"
                + "3️⃣ Ask your question or describe what confuses you\n"
                + "4️⃣ Click 'Get Help' and I'll guide you!\n\n"
                + "Let's master your subject! 🚀";
        messages.add(new Message(welcome, false));
        adapter.notifyDataSetChanged();

        // Set button listener
        btnGetHelp.setOnClickListener(v -> askTutor());
    }

    private void askTutor() {
        String subject = etSubject.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();

        // Validate inputs
        if (subject.isEmpty() || topic.isEmpty() || question.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user question to chat
        messages.add(new Message("📖 Subject: " + subject + "\n🎯 Topic: " + topic + "\n❓ Q: " + question, true));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);

        // Clear inputs
        etQuestion.setText("");

        // Show loading state
        messages.add(new Message("⏳ Adeon is preparing a comprehensive explanation...", false));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);

        // Get tutoring response from Adeon
        adeonAIService.tutor(subject, topic, question, new AdeonAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    // Remove loading message
                    if (messages.size() > 0 && messages.get(messages.size() - 1).getText().contains("preparing")) {
                        messages.remove(messages.size() - 1);
                    }
                    // Add tutoring response
                    messages.add(new Message(response, false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    if (messages.size() > 0 && messages.get(messages.size() - 1).getText().contains("preparing")) {
                        messages.remove(messages.size() - 1);
                    }
                    messages.add(new Message("Let me help you with this. Can you clarify what specifically confuses you about this topic?", false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }
        });
    }
}
