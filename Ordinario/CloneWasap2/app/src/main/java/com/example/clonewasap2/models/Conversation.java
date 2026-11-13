package com.example.clonewasap2.models;

public class Conversation {
    private String id;
    private boolean isGroup;
    private String name; // Transient field para mostrar en UI
    private String lastMessage; // Transient field para mostrar en UI

    public Conversation() {}

    public Conversation(String id, boolean isGroup, String name, String lastMessage) {
        this.id = id;
        this.isGroup = isGroup;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getId() { return id; }
    public boolean isGroup() { return isGroup; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}