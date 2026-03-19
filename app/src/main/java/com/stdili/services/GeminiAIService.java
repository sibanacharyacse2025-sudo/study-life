package com.stdili.services;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;

public class GeminiAIService {
    private static final String TAG = "GeminiAIService";
    private FirebaseFirestore db;

    public GeminiAIService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnResponse {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void generateStudyNotes(String subject, String topic, String content, OnResponse listener) {
        // Offline fallback (no API calls)
        Log.d(TAG, "Generating offline notes for: " + subject + " - " + topic);
        new LocalAIService().generateStudyNotes(subject, topic, content, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                listener.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    public void explainQuestion(String question, OnResponse listener) {
        Log.d(TAG, "Offline explain: " + question);
        new LocalAIService().chatReply(question, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                listener.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    public void generateContent(String prompt, OnResponse listener) {
        Log.d(TAG, "Offline generate: " + prompt);
        new LocalAIService().chatReply(prompt, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                listener.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }
}
