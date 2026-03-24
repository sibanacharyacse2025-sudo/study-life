package com.stdili.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SecureSessionManager {
    private static final String PREFS = "stdili_secure_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private final SharedPreferences prefs;

    public SecureSessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(String accessToken, String userId) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
