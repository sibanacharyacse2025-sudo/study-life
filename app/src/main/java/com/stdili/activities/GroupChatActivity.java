package com.stdili.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private List<Message> messages;
    private MessageAdapter adapter;
    private TextView tvTyping;
    private String groupId;
    private String myUserId;
    private Socket socket;
    private ApiClient apiClient;
    private MessageOutboxManager outboxManager;
    private boolean loadingHistory = false;
    private boolean hasMoreHistory = true;
    private String nextBefore;
    private String lastSentText = null;
    private long lastSentAtMs = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvTyping = findViewById(R.id.tvTyping);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null || groupId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid group session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        myUserId = new SecureSessionManager(this).getUserId();
        apiClient = ApiClient.getInstance(new SecureSessionManager(this));
        outboxManager = new MessageOutboxManager(this);
        socket = SocketManager.getInstance().getSocket();
        SocketManager.getInstance().connect();
        loadHistory(null);
        outboxManager.flush(apiClient, null);
        if (socket != null && groupId != null) {
            try {
                socket.emit("group:join", new JSONObject().put("groupId", groupId));
            } catch (Exception ignored) {
            }
            socket.on("group:message", args -> {
                if (args.length == 0) return;
                JSONObject msg = (JSONObject) args[0];
                if (!groupId.equals(msg.optString("groupId"))) return;
                String sender = msg.optString("senderId");
                String text = msg.optString("text");
                runOnUiThread(() -> {
                    if (myUserId != null && myUserId.equals(sender) && isLikelyEchoDuplicate(text)) {
                        return;
                    }
                    messages.add(new Message(text, myUserId != null && myUserId.equals(sender)));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            });
            socket.on("chat:typing", args -> runOnUiThread(() -> tvTyping.setVisibility(android.view.View.VISIBLE)));
        }

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (socket != null && myUserId != null && groupId != null) {
                    try {
                        JSONObject typing = new JSONObject();
                        typing.put("roomId", "group:" + groupId);
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
        if (text.isEmpty() || groupId == null || myUserId == null) return;
        messages.add(new Message(text, true));
        lastSentText = text;
        lastSentAtMs = System.currentTimeMillis();
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);
        try {
            JSONObject payload = new JSONObject();
            payload.put("groupId", groupId);
            payload.put("senderId", myUserId);
            payload.put("text", text);
            if (socket != null) socket.emit("group:message", payload);
        } catch (Exception ignored) {
        }
        JSONObject body = new JSONObject();
        try {
            body.put("text", text);
        } catch (Exception ignored) {
        }
        String endpoint = "/api/groups/" + groupId + "/messages";
        apiClient.post(endpoint, body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {}

            @Override
            public void onError(String message) {
                outboxManager.enqueue(endpoint, text);
                runOnUiThread(() -> Toast.makeText(GroupChatActivity.this, "Queued offline, will retry", Toast.LENGTH_SHORT).show());
            }
        });
        etMessage.setText("");
        tvTyping.setVisibility(android.view.View.GONE);
    }

    private void loadHistory(String before) {
        if (groupId == null) return;
        loadingHistory = true;
        String path = "/api/groups/" + groupId + "/messages?limit=20" + (before != null ? "&before=" + before : "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off("group:message");
            socket.off("chat:typing");
        }
    }

    private boolean isLikelyEchoDuplicate(String incomingText) {
        if (lastSentText == null) return false;
        return lastSentText.equals(incomingText) && (System.currentTimeMillis() - lastSentAtMs) < 3000L;
    }
}