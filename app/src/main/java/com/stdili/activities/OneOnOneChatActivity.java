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
    private String chatRoomId;

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
        chatService = new ChatService();

        // Get data from Intent
        otherUserId = getIntent().getStringExtra("userId");
        otherUserName = getIntent().getStringExtra("userName");
        currentUserId = "currentUserId"; // Replace with actual current user ID

        if (otherUserName != null) {
            setTitle(otherUserName);
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

        // Load messages from Firebase
        chatService.loadOneOnOneChat(currentUserId, otherUserId, new ChatService.OnMessagesLoadedListener() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                messageList.clear();
                messageList.addAll(messages);
                chatAdapter.setMessages(messages);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OneOnOneChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use conversation ID based on user IDs
        String conversationId = currentUserId.compareTo(otherUserId) < 0 
            ? currentUserId + "_" + otherUserId 
            : otherUserId + "_" + currentUserId;

        chatService.saveMessage(currentUserId, conversationId, messageText, true);
        messageInput.setText("");
        loadChatData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatService != null) {
            chatService.cleanup();
        }
    }
}
