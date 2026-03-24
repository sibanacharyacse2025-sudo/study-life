package com.stdili.network;

import com.stdili.BuildConfig;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final long CACHE_TTL_MS = 30_000L;
    private static final int MAX_RETRY_ATTEMPTS = 2;
    private static ApiClient instance;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .build();
    private final SecureSessionManager sessionManager;
    private final Map<String, CacheEntry> cache = new HashMap<>();

    private ApiClient(SecureSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static synchronized ApiClient getInstance(SecureSessionManager sessionManager) {
        if (instance == null) {
            instance = new ApiClient(sessionManager);
        }
        return instance;
    }

    public interface ApiCallback {
        void onSuccess(JSONObject data);
        void onError(String message);
    }

    public void get(String path, boolean useCache, ApiCallback callback) {
        String url = BuildConfig.BACKEND_API_BASE_URL + path;
        if (useCache && cache.containsKey(url)) {
            CacheEntry entry = cache.get(url);
            if (System.currentTimeMillis() - entry.timestamp < CACHE_TTL_MS) {
                callback.onSuccess(entry.data);
                return;
            }
            cache.remove(url);
        }
        Request.Builder builder = new Request.Builder().url(url).get();
        attachAuth(builder);
        execute(builder.build(), useCache, callback, 0);
    }

    public void post(String path, JSONObject body, ApiCallback callback) {
        Request.Builder builder = new Request.Builder()
                .url(BuildConfig.BACKEND_API_BASE_URL + path)
                .post(RequestBody.create(body.toString(), JSON));
        attachAuth(builder);
        execute(builder.build(), false, callback, 0);
    }

    public void patch(String path, JSONObject body, ApiCallback callback) {
        Request.Builder builder = new Request.Builder()
                .url(BuildConfig.BACKEND_API_BASE_URL + path)
                .patch(RequestBody.create(body.toString(), JSON));
        attachAuth(builder);
        execute(builder.build(), false, callback, 0);
    }

    private void attachAuth(Request.Builder builder) {
        String token = sessionManager.getAccessToken();
        if (token != null && !"firebase_session".equals(token)) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
    }

    private void execute(Request request, boolean storeCache, ApiCallback callback, int attempt) {
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    execute(request, storeCache, callback, attempt + 1);
                    return;
                }
                callback.onError("Network timeout. Please retry.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                try {
                    JSONObject json = new JSONObject(responseBody);
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            sessionManager.clear();
                            callback.onError("Session expired. Please login again.");
                            return;
                        }
                        callback.onError(json.optString("error", "Request failed"));
                        return;
                    }
                    if (storeCache) {
                        cache.put(request.url().toString(), new CacheEntry(json, System.currentTimeMillis()));
                    }
                    callback.onSuccess(json);
                } catch (Exception e) {
                    callback.onError("Unexpected server response");
                }
            }
        });
    }

    private static class CacheEntry {
        final JSONObject data;
        final long timestamp;

        CacheEntry(JSONObject data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }
}
