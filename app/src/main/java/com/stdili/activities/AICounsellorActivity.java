package com.stdili.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stdili.R;
import com.stdili.adapters.MessageAdapter;
import com.stdili.models.Message;
import com.stdili.models.UserProgress;
import com.stdili.services.AdeonAIService;
import com.stdili.services.ChatService;
import com.stdili.services.EnhancedAdeonService;
import com.stdili.services.LocalAIService;
import com.stdili.utils.ModerationUtils;
import com.stdili.utils.NotificationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private TextToSpeech textToSpeech;
    private boolean isListening = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final AdeonAIService adeonAIService = new AdeonAIService();
    private final LocalAIService localAIService = new LocalAIService();
    private EnhancedAdeonService enhancedAdeonService;
    private ChatService chatService;
    private String conversationId;


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

        String uid = FirebaseAuth.getInstance().getUid();
        conversationId = (uid != null ? uid + "_adeon_history" : "anon_adeon_history");
        chatService = new ChatService(this);
        enhancedAdeonService = new EnhancedAdeonService(this);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        // Set feedback listener
        adapter.setOnFeedbackListener((position, isPositive) -> {
            Message message = messages.get(position);
            saveFeedback(message.getText(), isPositive);
        });

        setupMoodSelector();
        setupSpeechRecognizer();
        setupTextToSpeech();

        btnSend.setOnClickListener(v -> sendMessage());
        btnVoice.setOnClickListener(v -> toggleVoiceInput());
        btnQuickHelp.setOnClickListener(v -> showStudyTips());

        // Add welcome message with Adeon's introduction
        String welcomeMessage = "🎓 **Welcome to ADEON!** 🎓\n\n"
                + "I'm **Adeon**, your intelligent study companion."
                + "\n\nI'm specially trained to help you with:\n"
                + "📚 **Learning** - Any subject (Math, Science, History, Languages)\n"
                + "💡 **Problem-Solving** - Complex topics explained step-by-step\n"
                + "📝 **Study Strategies** - Notes, exams, memory techniques\n"
                + "💪 **Motivation** - Beat procrastination & stress\n"
                + "💙 **Counseling** - Emotional support for your journey\n\n"
                + "What can I help you with today? Ask anything! 🚀";
        messages.add(new Message(welcomeMessage, false));
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
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

            String lower = text.toLowerCase(Locale.ROOT);
            
            // Handle personality mode switching
            if (lower.contains("strict tutor")) {
                adeonAIService.setPersonality(com.stdili.models.AdeonPersonality.TutorMode.STRICT_TUTOR);
                messages.add(new Message("Adeon switched to Strict Tutor mode.", false));
                adapter.notifyDataSetChanged();
            } else if (lower.contains("friendly coach")) {
                adeonAIService.setPersonality(com.stdili.models.AdeonPersonality.TutorMode.FRIENDLY_COACH);
                messages.add(new Message("Adeon switched to Friendly Coach mode.", false));
                adapter.notifyDataSetChanged();
            }

            // Handle special commands
            if (lower.contains("generate notes") || lower.contains("notes")) {
                handleGenerateNotes(text);
            } else if (lower.contains("generate practice") || lower.contains("practice questions")) {
                handleGeneratePractice(text);
            } else if (lower.contains("analyze progress") || lower.contains("progress")) {
                handleAnalyzeProgress();
            } else if (lower.contains("create plan")) {
                handleCreatePlan(text);
            } else {
                // Regular chat
                String uid = FirebaseAuth.getInstance().getUid();
                if (uid != null && chatService != null) {
                    chatService.saveMessage(uid, conversationId, text, true);
                }

                maybeNotifyPlan(text);
                generateLocalResponse(text);
            }
        }
    }

    private void handleGenerateNotes(String userInput) {
        // Extract subject and topic from input (simple parsing)
        messages.add(new Message("📝 Generating structured notes...", false));
        adapter.notifyDataSetChanged();

        enhancedAdeonService.generateStructuredNotes("Mathematics", "Algebra", 
                "Algebra is fundamental to mathematics...", new EnhancedAdeonService.OnNotesGenerated() {
            @Override
            public void onSuccess(com.stdili.models.StudyNotes notes) {
                String formattedNotes = notes.toFormattedString();
                removeTypingIfPresent();
                messages.add(new Message(formattedNotes, false));
                adapter.notifyDataSetChanged();
                rvMessages.smoothScrollToPosition(messages.size() - 1);
                speakText("Notes generated successfully. Review the detailed content above.");
            }

            @Override
            public void onFailure(String error) {
                removeTypingIfPresent();
                messages.add(new Message("Failed to generate notes: " + error, false));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void handleGeneratePractice(String userInput) {
        messages.add(new Message("🎯 Generating 10 practice questions...", false));
        adapter.notifyDataSetChanged();

        enhancedAdeonService.generatePracticeQuestions("Mathematics", "Algebra",
                new EnhancedAdeonService.OnPracticeGenerated() {
            @Override
            public void onSuccess(List<com.stdili.models.PracticeQuestion> questions) {
                removeTypingIfPresent();
                StringBuilder allQuestions = new StringBuilder();
                allQuestions.append("📋 PRACTICE SET: 10 Questions (Easy 3, Medium 4, Hard 3)\n");
                allQuestions.append("═════════════════════════════════════════════\n");
                
                for (com.stdili.models.PracticeQuestion q : questions) {
                    allQuestions.append(q.toFormattedString());
                }
                
                messages.add(new Message(allQuestions.toString(), false));
                adapter.notifyDataSetChanged();
                rvMessages.smoothScrollToPosition(messages.size() - 1);
                speakText("Practice questions generated. Start solving and improve your accuracy.");
            }

            @Override
            public void onFailure(String error) {
                removeTypingIfPresent();
                messages.add(new Message("Failed to generate practice: " + error, false));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void handleAnalyzeProgress() {
        messages.add(new Message("📊 Analyzing your progress...", false));
        adapter.notifyDataSetChanged();

        enhancedAdeonService.analyzeProgress(new EnhancedAdeonService.OnProgressAnalyzed() {
            @Override
            public void onSuccess(String analysis) {
                removeTypingIfPresent();
                messages.add(new Message(analysis, false));
                adapter.notifyDataSetChanged();
                rvMessages.smoothScrollToPosition(messages.size() - 1);
                speakText(analysis.substring(0, Math.min(300, analysis.length())));
            }

            @Override
            public void onFailure(String error) {
                removeTypingIfPresent();
                messages.add(new Message("Failed to analyze: " + error, false));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void handleCreatePlan(String userInput) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            enhancedAdeonService.createPlan(uid, "Study Plan", 
                    java.util.Arrays.asList("Math", "Science"), 2, "2026-03-28");
            
            messages.add(new Message("✅ Study plan created!\n" +
                    "📱 You'll receive daily reminders\n" +
                    "🔔 Notifications for missed goals\n" +
                    "⏰ Accountability checks\n" +
                    "🎯 Track your progress daily", false));
            adapter.notifyDataSetChanged();
            speakText("Study plan created successfully. Check notifications for daily reminders.");
        }
    }

    private void generateLocalResponse(String prompt) {
        // Show a loading indicator or typing animation
        messages.add(new Message("...", false));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);

        adeonAIService.chat(prompt, new AdeonAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    removeTypingIfPresent();
                    messages.add(new Message(response, false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                    speakText(response);

                    String uid = FirebaseAuth.getInstance().getUid();
                    if (uid != null && chatService != null) {
                        chatService.saveMessage(uid, conversationId, response, false);
                    }

                    if (response.toLowerCase(Locale.ROOT).contains("senior")) {
                        maybeNotifySenior(response);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                localAIService.counsellorReply(prompt, new LocalAIService.OnResponse() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            removeTypingIfPresent();
                            messages.add(new Message(response, false));
                            adapter.notifyDataSetChanged();
                            rvMessages.smoothScrollToPosition(messages.size() - 1);
                            speakText(response);
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
        });
    }

    private void removeTypingIfPresent() {
        if (messages.size() > 0 && "...".equals(messages.get(messages.size() - 1).getText())) {
            messages.remove(messages.size() - 1);
        }
    }

    private void maybeNotifyPlan(String prompt) {
        if (prompt == null) return;
        String s = prompt.toLowerCase();
        boolean isPlan = s.contains("plan") || s.contains("timetable") || s.contains("schedule") || s.contains("routine");
        if (!isPlan) return;

        String uid = FirebaseAuth.getInstance().getUid();
        new NotificationHandler(this).notifyUser(uid, "Time to study! Open your daily plan");
    }

    private void maybeNotifySenior(String aiAnswer) {
        if (aiAnswer == null || aiAnswer.trim().isEmpty()) return;
        if (aiAnswer.toLowerCase(Locale.ROOT).contains("senior")) {
            String uid = FirebaseAuth.getInstance().getUid();
            new NotificationHandler(this).notifyUser(uid, "Senior-style advice available: check your Adeon chat updates");
        }
    }

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.SUCCESS) {
                Log.e("AICounsellorActivity", "TextToSpeech initialization failed");
            } else {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
    }

    private void speakText(String text) {
        if (textToSpeech == null || textToSpeech.isSpeaking()) return;
        
        // Clean up markdown and emojis for TTS
        String cleanText = text
                .replaceAll("[#*_`~【】【】═]", "")
                .replaceAll("[🎓📚💡💙🎯📷🌍🧾🔔⏰🌟⭐👍📚❌⛔🎉🚀]", "")
                .replaceAll("\\n+", ". ");
        
        if (cleanText.length() > 200) {
            cleanText = cleanText.substring(0, 200) + "...";
        }
        
        textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "AdeonTTS");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void saveFeedback(String aiResponse, boolean isPositive) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("feedback")
                .add(new Object() {
                    public String userId = uid;
                    public String response = aiResponse;
                    public boolean positive = isPositive;
                    public long timestamp = System.currentTimeMillis();
                })
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("AICounsellorActivity", "Failed to save feedback", e);
                });
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

}