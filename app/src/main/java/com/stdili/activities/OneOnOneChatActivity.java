package com.stdili.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stdili.R;
import com.stdili.adapters.ChatMessageAdapter;
import com.stdili.models.ChatMessage;
import com.stdili.services.ChatService;
import com.stdili.utils.FirebaseHelper;
import com.stdili.utils.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OneOnOneChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatMessageAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private ChatService chatService;
    private String otherUserId;
    private String otherUserName;
    private String currentUserId;
    private String currentUserName;
    private ListenerRegistration messagesListener;

    private boolean isResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_on_one_chat);

        initializeViews();
        setupRecyclerView();
        loadChatData();
    }

    private void initializeViews() {
        chatRecyclerView = findViewById(R.id.rvMessages);
        messageInput = findViewById(R.id.etMessage);
        sendButton = findViewById(R.id.btnSend);
        chatService = new ChatService(this);

        // Get data from Intent
        otherUserId = getIntent().getStringExtra("userId");
        otherUserName = getIntent().getStringExtra("userName");
        currentUserId = FirebaseAuth.getInstance().getUid();

        if (otherUserName != null) setTitle(otherUserName);

        if (currentUserId != null && otherUserId != null) {
            FirebaseHelper.getUser(currentUserId, user -> {
                if (user != null) {
                    currentUserName = user.getName();
                    if (currentUserName == null || currentUserName.trim().isEmpty()) {
                        currentUserName = user.getEmail();
                    }
                    if (currentUserName == null || currentUserName.trim().isEmpty()) {
                        currentUserName = "User";
                    }
                }
            });
        }

        // Set click listener for send button
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatMessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void loadChatData() {
        if (otherUserId == null) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Real-time listener for incoming messages
        String chatRoomId = getChatRoomId(currentUserId, otherUserId);

        messagesListener = FirebaseFirestore.getInstance()
                .collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Toast.makeText(OneOnOneChatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snap == null) return;

                    int previousSize = messageList.size();
                    List<ChatMessage> updated = new ArrayList<>();
                    snap.getDocuments().forEach(doc -> {
                        ChatMessage msg = doc.toObject(ChatMessage.class);
                        msg.setMessageId(doc.getId());
                        updated.add(msg);
                    });

                    messageList.clear();
                    messageList.addAll(updated);
                    chatAdapter.setMessages(messageList);

                    if (updated.size() > previousSize) {
                        // New messages appended at the end
                        ChatMessage newest = updated.get(updated.size() - 1);
                        if (newest != null && newest.getSenderId() != null && !newest.getSenderId().equals(currentUserId)) {
                            if (!isResumed) {
                                new NotificationHandler(OneOnOneChatActivity.this)
                                        .notifyUser(currentUserId, (newest.getSenderName() == null ? "New message" : newest.getSenderName()) + ": " + newest.getMessageText());
                            }
                        }
                    }

                    if (!messageList.isEmpty()) {
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });

        // Chat updates are handled by the snapshot listener above.
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUserName == null || currentUserName.trim().isEmpty()) currentUserName = "User";

        chatService.saveOneOnOneMessage(currentUserId, currentUserName, otherUserId, messageText);
        messageInput.setText("");
    }

    private String getChatRoomId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        }
        return userId2 + "_" + userId1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
        if (chatService != null) chatService.cleanup();
    }
}
