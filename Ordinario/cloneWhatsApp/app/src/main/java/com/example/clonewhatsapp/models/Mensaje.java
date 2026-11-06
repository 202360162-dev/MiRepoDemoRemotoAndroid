package com.example.clonewhatsapp.models;

public class Mensaje {
    private String id;
    private String senderId;
    private String content;
    private String estado; // enviado, recibido, leido

    public Mensaje(String id, String senderId, String content, String estado) {
        this.id = id; this.senderId = senderId; this.content = content; this.estado = estado;
    }

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public String getEstado() { return estado; }
}