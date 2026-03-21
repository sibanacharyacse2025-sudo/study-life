package com.stdili.services;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stdili.models.ChatMessage;
import com.stdili.models.CommunityGroup;
import com.stdili.models.Message;
import com.stdili.utils.EncryptionUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing chat conversations, one-on-one messaging, and group chats with Firestore
 */
public class ChatService {
    private static final String TAG = "ChatService";
    private FirebaseFirestore db;
    private EncryptionUtil encryptionUtil;

    public ChatService(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.encryptionUtil = new EncryptionUtil(context);
    }

    /**
     * Save a single one-on-one message between junior and senior
     */
    public void saveOneOnOneMessage(String senderId, String senderName, String receiverId, String messageText) {
        try {
            // Encrypt the message
            String encryptedText = encryptionUtil.encrypt(messageText);

            // Create a chatroom ID based on both users
            String chatRoomId = getChatRoomId(senderId, receiverId);

            ChatMessage chatMessage = new ChatMessage(senderId, senderName, receiverId, encryptedText);
            chatMessage.setChatRoomId(chatRoomId);

            db.collection("chatRooms")
                    .document(chatRoomId)
                    .collection("messages")
                    .add(chatMessage)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "One-on-one message saved: " + documentReference.getId());
                        // Update last message timestamp for chatroom
                        updateChatRoomTimestamp(chatRoomId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving one-on-one message", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving one-on-one message", e);
        }
    }

    /**
     * Load one-on-one chat messages between two users
     */
    public void loadOneOnOneChat(String userId, String otherUserId, OnMessagesLoadedListener listener) {
        try {
            String chatRoomId = getChatRoomId(userId, otherUserId);

            db.collection("chatRooms")
                    .document(chatRoomId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<ChatMessage> messages = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            msg.setMessageId(doc.getId());
                            // Decrypt the message
                            msg.setMessageText(encryptionUtil.decrypt(msg.getMessageText()));
                            messages.add(msg);
                        });
                        listener.onMessagesLoaded(messages);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading one-on-one chat", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading one-on-one chat", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Get list of all chat rooms for a user
     */
    public void getUserChatRooms(String userId, OnChatRoomsLoadedListener listener) {
        try {
            db.collection("chatRooms")
                    .whereArrayContains("participants", userId)
                    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Map<String, Object>> chatRooms = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            chatRooms.add(doc.getData());
                        });
                        listener.onChatRoomsLoaded(chatRooms);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading chat rooms", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading chat rooms", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Save message to a community group
     */
    public void saveGroupMessage(String groupId, String senderId, String senderName, String messageText) {
        try {
            // Encrypt the message
            String encryptedText = encryptionUtil.encrypt(messageText);

            ChatMessage chatMessage = new ChatMessage(senderId, senderName, null, encryptedText);
            chatMessage.setChatRoomId(groupId);

            db.collection("communityGroups")
                    .document(groupId)
                    .collection("messages")
                    .add(chatMessage)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Group message saved to: " + groupId);
                        updateGroupLastMessageTime(groupId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving group message", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving group message", e);
        }
    }

    /**
     * Load group chat messages
     */
    public void loadGroupMessages(String groupId, OnMessagesLoadedListener listener) {
        try {
            db.collection("communityGroups")
                    .document(groupId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<ChatMessage> messages = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            msg.setMessageId(doc.getId());
                            // Decrypt the message
                            msg.setMessageText(encryptionUtil.decrypt(msg.getMessageText()));
                            messages.add(msg);
                        });
                        listener.onMessagesLoaded(messages);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading group messages", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading group messages", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Create a new community group (only teachers and seniors can create)
     */
    public void createCommunityGroup(String groupName, String groupDescription, String createdBy, 
                                    String creatorRole, String category, OnGroupCreatedListener listener) {
        try {
            // Validate that only teachers and seniors can create
            if (!creatorRole.equals("teacher") && !creatorRole.equals("senior")) {
                listener.onError("Only teachers and seniors can create groups");
                return;
            }

            CommunityGroup group = new CommunityGroup(groupName, createdBy, creatorRole);
            group.setGroupDescription(groupDescription);
            group.setCategory(category);

            db.collection("communityGroups")
                    .add(group)
                    .addOnSuccessListener(documentReference -> {
                        // Update the group ID
                        documentReference.update("groupId", documentReference.getId());
                        Log.d(TAG, "Community group created: " + documentReference.getId());
                        listener.onGroupCreated(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating community group", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating community group", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Get all community groups
     */
    public void getCommunityGroups(OnGroupsLoadedListener listener) {
        try {
            db.collection("communityGroups")
                    .whereEqualTo("isActive", true)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<CommunityGroup> groups = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            CommunityGroup group = doc.toObject(CommunityGroup.class);
                            group.setGroupId(doc.getId());
                            groups.add(group);
                        });
                        listener.onGroupsLoaded(groups);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading community groups", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading community groups", e);
            listener.onError(e.getMessage());
        }
    }

    public void updateGroup(CommunityGroup group, OnOperationCompleteListener listener) {
        try {
            db.collection("communityGroups")
                    .document(group.getGroupId())
                    .set(group)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Community group updated: " + group.getGroupId());
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating community group", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while updating community group", e);
            listener.onError(e.getMessage());
        }
    }

    public void createCommunityGroup(CommunityGroup group, OnOperationCompleteListener listener) {
        try {
            db.collection("communityGroups")
                    .add(group)
                    .addOnSuccessListener(documentReference -> {
                        group.setGroupId(documentReference.getId());
                        documentReference.update("groupId", documentReference.getId());
                        Log.d(TAG, "Community group created: " + documentReference.getId());
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating community group", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating community group", e);
            listener.onError(e.getMessage());
        }
    }

    public void cleanup() {
        // Implementation for cleanup if needed
    }

    /**
     * Save an individual conversation (AI chat history)
     */
    public void saveMessage(String userId, String conversationId, String messageText, boolean isUser) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("text", messageText);
            message.put("isUser", isUser);
            message.put("timestamp", new Date());
            message.put("userId", userId);

            db.collection("users")
                    .document(userId)
                    .collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "AI conversation message saved: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving AI message", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving AI message", e);
        }
    }

    /**
     * Helper method to generate consistent chat room ID
     */
    private String getChatRoomId(String userId1, String userId2) {
        // Sort the IDs to ensure same chatroom regardless of order
        if (userId1.compareTo(userId2) > 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    private void updateChatRoomTimestamp(String chatRoomId) {
        Map<String, Object> update = new HashMap<>();
        update.put("lastMessageTime", new Date());

        db.collection("chatRooms")
                .document(chatRoomId)
                .update(update)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating chatroom timestamp", e));
    }

    private void updateGroupLastMessageTime(String groupId) {
        Map<String, Object> update = new HashMap<>();
        update.put("lastMessageTime", new Date());

        db.collection("communityGroups")
                .document(groupId)
                .update(update)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating group timestamp", e));
    }

    // Listener Interfaces
    public interface OnMessagesLoadedListener {
        void onMessagesLoaded(List<ChatMessage> messages);
        void onError(String errorMessage);
    }

    public interface OnChatRoomsLoadedListener {
        void onChatRoomsLoaded(List<Map<String, Object>> chatRooms);
        void onError(String errorMessage);
    }

    public interface OnGroupCreatedListener {
        void onGroupCreated(String groupId);
        void onError(String errorMessage);
    }

    public interface OnGroupsLoadedListener {
        void onGroupsLoaded(List<CommunityGroup> groups);
        void onError(String errorMessage);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onError(String errorMessage);
    }
}
