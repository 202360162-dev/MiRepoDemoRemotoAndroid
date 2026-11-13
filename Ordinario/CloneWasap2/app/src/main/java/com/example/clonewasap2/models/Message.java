package com.example.clonewasap2.models;

public class Message {
    private String id;
    private String conversationId;
    private String senderId;
    private String content;
    private String status;

    public Message(String id, String conversationId, String senderId, String content, String status) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.status = status;
    }

    public String getId() { return id; }
    public String getConversationId() { return conversationId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
}