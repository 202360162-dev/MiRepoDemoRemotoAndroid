package com.example.clonewasap2.models;

public class User {
    private String id;
    private String name;
    private String avatar_url;

    public User(String id, String name, String avatar_url) {
        this.id = id;
        this.name = name;
        this.avatar_url = avatar_url;
    }
    // Agrega Getters y Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatar_url; }
}