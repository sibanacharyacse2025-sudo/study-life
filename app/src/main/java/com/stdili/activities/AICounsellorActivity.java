package com.stdili.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.MessageAdapter;
import com.stdili.models.Message;
import com.stdili.services.LocalAIService;
import com.stdili.utils.ModerationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AICounsellorActivity extends AppCompatActivity implements RecognitionListener {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnVoice;
    private Button btnQuickHelp;
    private LinearLayout llMoodSelector;
    private List<Message> messages;
    private MessageAdapter adapter;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final LocalAIService localAIService = new LocalAIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_counsellor);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnVoice = findViewById(R.id.btnVoice);
        btnQuickHelp = findViewById(R.id.btnQuickHelp);
        llMoodSelector = findViewById(R.id.llMoodSelector);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        setupMoodSelector();
        setupSpeechRecognizer();

        btnSend.setOnClickListener(v -> sendMessage());
        btnVoice.setOnClickListener(v -> toggleVoiceInput());
        btnQuickHelp.setOnClickListener(v -> showStudyTips());

        // Add welcome message with emoji
        messages.add(new Message("👋 Hey there! I'm StudyLife, your AI learning buddy. I'm here to help with studying, motivation, or just to chat. What can I help you with today? 📚", false));
        adapter.notifyDataSetChanged();
    }

    private void showStudyTips() {
        String[] tips = {
            "🍅 Pomodoro Technique: Study for 25 minutes, then take a 5-minute break. This keeps your focus sharp!",
            "💧 Stay hydrated! Drink water every hour. Your brain works 10% better when hydrated.",
            "📱 Turn off notifications while studying. Even a small buzz can break your concentration.",
            "🎓 Teach it to learn it! Explaining concepts to others helps you master them faster.",
            "🧹 A clean desk = a clear mind. Keep your study space organized. The environment matters!",
            "😴 Sleep is crucial! Don't sacrifice sleep for study. Aim for 7-9 hours daily.",
            "🗂️ Use active recall: Test yourself instead of just re-reading notes.",
            "📝 Summarize what you learned daily. This strengthens memory retention.",
            "🚶 Take a walk after studying. Movement helps consolidate learning.",
            "🎯 Set SMART goals: Specific, Measurable, Achievable, Relevant, Time-bound."
        };
        int randomIndex = (int) (Math.random() * tips.length);
        messages.add(new Message(tips[randomIndex], false));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);
    }

    private void setupMoodSelector() {
        String[] moods = {"Happy 😊", "Sad 😢", "Anxious 😰", "Stressed 😫", "Excited 🤩", "Tired 😴"};
        for (String mood : moods) {
            Button moodButton = new Button(this);
            moodButton.setText(mood);
            moodButton.setOnClickListener(v -> selectMood(mood));
            llMoodSelector.addView(moodButton);
        }
    }

    private void selectMood(String mood) {
        messages.add(new Message("I'm feeling " + mood, true));
        adapter.notifyDataSetChanged();

        generateLocalResponse("The user says they are feeling " + mood + ".");
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
    }

    private void toggleVoiceInput() {
        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        speechRecognizer.startListening(intent);
        isListening = true;
        btnVoice.setImageResource(R.drawable.ic_mic_on);
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
    }

    private void stopListening() {
        speechRecognizer.stopListening();
        isListening = false;
        btnVoice.setImageResource(R.drawable.ic_mic_off);
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            if (ModerationUtils.containsBadWords(text)) {
                Toast.makeText(this, "Please keep the conversation appropriate.", Toast.LENGTH_SHORT).show();
                return;
            }

            messages.add(new Message(text, true));
            adapter.notifyDataSetChanged();
            etMessage.setText("");

            generateLocalResponse(text);
        }
    }

    private void generateLocalResponse(String prompt) {
        // Show a loading indicator or typing animation
        messages.add(new Message("...", false));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);

        localAIService.counsellorReply(prompt, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    removeTypingIfPresent();
                    messages.add(new Message(response, false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    removeTypingIfPresent();
                    messages.add(new Message("I’m here with you. Tell me what’s going on, and we’ll take it step by step.", false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }
        });
    }

    private void removeTypingIfPresent() {
        if (messages.size() > 0 && "...".equals(messages.get(messages.size() - 1).getText())) {
            messages.remove(messages.size() - 1);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onError(int error) {
        stopListening();
        Toast.makeText(this, "Speech recognition error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(Bundle results) {
        stopListening();
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            etMessage.setText(matches.get(0));
            sendMessage();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {}

    @Override
    public void onEvent(int eventType, Bundle params) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}