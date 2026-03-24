package com.stdili.network;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageOutboxManager {
    private static final String PREFS = "stdili_outbox";
    private static final String KEY_ITEMS = "items";
    private final SharedPreferences prefs;

    public interface FlushListener {
        void onFlushed();
    }

    public MessageOutboxManager(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void enqueue(String endpoint, String text) {
        JSONArray items = readItems();
        JSONObject obj = new JSONObject();
        try {
            obj.put("endpoint", endpoint);
            obj.put("text", text);
            obj.put("retryCount", 0);
            obj.put("nextAttemptAt", System.currentTimeMillis());
        } catch (Exception ignored) {
        }
        items.put(obj);
        saveItems(items);
    }

    public void flush(ApiClient apiClient, FlushListener listener) {
        flushAtIndex(apiClient, 0, listener);
    }

    private void flushAtIndex(ApiClient apiClient, int index, FlushListener listener) {
        JSONArray items = readItems();
        if (index >= items.length()) {
            if (listener != null) listener.onFlushed();
            return;
        }
        JSONObject item = items.optJSONObject(index);
        if (item == null) {
            flushAtIndex(apiClient, index + 1, listener);
            return;
        }
        long nextAttemptAt = item.optLong("nextAttemptAt", 0);
        if (System.currentTimeMillis() < nextAttemptAt) {
            flushAtIndex(apiClient, index + 1, listener);
            return;
        }
        JSONObject body = new JSONObject();
        try {
            body.put("text", item.optString("text"));
        } catch (Exception ignored) {
        }
        apiClient.post(item.optString("endpoint"), body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                removeAt(index);
                flushAtIndex(apiClient, index, listener);
            }

            @Override
            public void onError(String message) {
                updateRetry(index);
                flushAtIndex(apiClient, index + 1, listener);
            }
        });
    }

    private void removeAt(int index) {
        JSONArray items = readItems();
        JSONArray next = new JSONArray();
        for (int i = 0; i < items.length(); i++) {
            if (i == index) continue;
            next.put(items.opt(i));
        }
        saveItems(next);
    }

    private void updateRetry(int index) {
        JSONArray items = readItems();
        JSONObject item = items.optJSONObject(index);
        if (item == null) return;
        int retryCount = item.optInt("retryCount", 0) + 1;
        long delayMs = Math.min(30_000L, (long) Math.pow(2, retryCount) * 1000L);
        try {
            item.put("retryCount", retryCount);
            item.put("nextAttemptAt", System.currentTimeMillis() + delayMs);
        } catch (Exception ignored) {
        }
        saveItems(items);
    }

    private JSONArray readItems() {
        try {
            String raw = prefs.getString(KEY_ITEMS, "[]");
            return new JSONArray(raw);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void saveItems(JSONArray array) {
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply();
    }
}
