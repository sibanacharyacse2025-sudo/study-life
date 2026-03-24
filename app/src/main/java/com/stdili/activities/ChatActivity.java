package com.stdili.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.MessageAdapter;
import com.stdili.models.Message;
import com.stdili.network.ApiClient;
import com.stdili.network.MessageOutboxManager;
import com.stdili.network.SocketManager;
import com.stdili.utils.SecureSessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvTyping;
    private TextView tvChatTitle;
    private final List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private Socket socket;
    private String myUserId;
    private String mentorId;
    private ApiClient apiClient;
    private MessageOutboxManager outboxManager;
    private String nextBefore;
    private boolean loadingHistory = false;
    private boolean hasMoreHistory = true;
    private String lastSentText = null;
    private long lastSentAtMs = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvTyping = findViewById(R.id.tvTyping);
        tvChatTitle = findViewById(R.id.tvChatTitle);

        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        mentorId = getIntent().getStringExtra("mentor_id");
        if (mentorId == null || mentorId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid mentor session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String mentorName = getIntent().getStringExtra("mentor_name");
        tvChatTitle.setText(mentorName == null ? "Mentor Chat" : "Chat with " + mentorName);

        myUserId = new SecureSessionManager(this).getUserId();
        apiClient = ApiClient.getInstance(new SecureSessionManager(this));
        outboxManager = new MessageOutboxManager(this);
        socket = SocketManager.getInstance().getSocket();
        SocketManager.getInstance().connect();
        loadHistory(null);
        outboxManager.flush(apiClient, null);

        if (socket != null) {
            try {
                socket.emit("auth:join", new JSONObject().put("userId", myUserId));
            } catch (Exception ignored) {
            }
            socket.on("direct:message", args -> {
                if (args.length == 0) return;
                JSONObject json = (JSONObject) args[0];
                String senderId = json.optString("senderId");
                String receiverId = json.optString("receiverId");
                String text = json.optString("text");
                if (!matchesConversation(senderId, receiverId)) return;
                runOnUiThread(() -> {
                    if (myUserId != null && myUserId.equals(senderId) && isLikelyEchoDuplicate(text)) {
                        return;
                    }
                    messages.add(new Message(text, myUserId != null && myUserId.equals(senderId)));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            });
            socket.on("chat:typing", args -> runOnUiThread(() -> tvTyping.setVisibility(android.view.View.VISIBLE)));
        }

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (socket != null && mentorId != null && myUserId != null) {
                    try {
                        JSONObject typing = new JSONObject();
                        typing.put("roomId", myUserId + ":" + mentorId);
                        typing.put("userId", myUserId);
                        typing.put("isTyping", s.length() > 0);
                        socket.emit("chat:typing", typing);
                    } catch (Exception ignored) {
                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> sendMessage());
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null && lm.findFirstVisibleItemPosition() == 0 && hasMoreHistory && !loadingHistory) {
                    loadHistory(nextBefore);
                }
            }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty() || myUserId == null || mentorId == null) return;
        messages.add(new Message(text, true));
        lastSentText = text;
        lastSentAtMs = System.currentTimeMillis();
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);
        try {
            JSONObject payload = new JSONObject();
            payload.put("senderId", myUserId);
            payload.put("receiverId", mentorId);
            payload.put("text", text);
            if (socket != null) socket.emit("direct:message", payload);
        } catch (Exception ignored) {
        }
        JSONObject body = new JSONObject();
        try {
            body.put("text", text);
        } catch (Exception ignored) {
        }
        String endpoint = "/api/chat/direct/" + mentorId + "/messages";
        apiClient.post(endpoint, body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {}

            @Override
            public void onError(String message) {
                outboxManager.enqueue(endpoint, text);
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Queued offline, will retry", Toast.LENGTH_SHORT).show());
            }
        });
        etMessage.setText("");
        tvTyping.setVisibility(android.view.View.GONE);
    }

    private void loadHistory(String before) {
        if (mentorId == null) return;
        loadingHistory = true;
        String path = "/api/chat/direct/" + mentorId + "/messages?limit=20" + (before != null ? "&before=" + before : "");
        apiClient.get(path, false, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    JSONArray arr = data.optJSONArray("messages");
                    if (arr != null && arr.length() > 0) {
                        List<Message> older = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject m = arr.optJSONObject(i);
                            if (m == null) continue;
                            String senderId = m.optString("senderId");
                            String text = m.optString("text");
                            older.add(new Message(text, myUserId != null && myUserId.equals(senderId)));
                        }
                        messages.addAll(0, older);
                        adapter.notifyDataSetChanged();
                    }
                    JSONObject page = data.optJSONObject("page");
                    hasMoreHistory = page != null && page.optBoolean("hasMore", false);
                    nextBefore = page != null ? page.optString("nextBefore", null) : null;
                    loadingHistory = false;
                });
            }

            @Override
            public void onError(String message) {
                loadingHistory = false;
            }
        });
    }

    private boolean matchesConversation(String senderId, String receiverId) {
        if (myUserId == null || mentorId == null) return false;
        return (myUserId.equals(senderId) && mentorId.equals(receiverId))
                || (mentorId.equals(senderId) && myUserId.equals(receiverId));
    }

    private boolean isLikelyEchoDuplicate(String incomingText) {
        if (lastSentText == null) return false;
        return lastSentText.equals(incomingText) && (System.currentTimeMillis() - lastSentAtMs) < 3000L;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off("direct:message");
            socket.off("chat:typing");
        }
    }
}