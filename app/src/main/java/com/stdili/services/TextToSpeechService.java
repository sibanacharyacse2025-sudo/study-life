package com.stdili.services;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TextToSpeechService implements TextToSpeech.OnInitListener {
    private static final String TAG = "TextToSpeechService";
    private TextToSpeech tts;
    private boolean isReady = false;

    public TextToSpeechService(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("en", "IN"));
            tts.setPitch(0.95f);
            tts.setSpeechRate(0.8f);
            isReady = true;
            Log.d(TAG, "TextToSpeech initialized successfully");
        } else {
            Log.e(TAG, "TextToSpeech initialization failed");
        }
    }

    public void speak(String text) {
        if (isReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Log.d(TAG, "Speaking: " + text);
        }
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public boolean isReady() {
        return isReady;
    }
}
