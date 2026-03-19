package com.stdili.models;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private String messageId;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String receiverId;
    private String messageText;
    private String messageType; // "text", "image", "file"
    private Date timestamp;
    private boolean isRead;
    private String chatRoomId;

    public ChatMessage() {}

    public ChatMessage(String senderId, String senderName, String receiverId, String messageText) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.messageType = "text";
        this.timestamp = new Date();
        this.isRead = false;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }
}
