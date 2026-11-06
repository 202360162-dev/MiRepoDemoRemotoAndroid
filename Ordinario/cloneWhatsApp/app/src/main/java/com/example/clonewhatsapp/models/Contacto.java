package com.example.clonewhatsapp.models;

public class Contacto {
    private String id;
    private String email;
    private String nombre;
    private String fotoUrl;

    public Contacto(String id, String email, String nombre, String fotoUrl) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.fotoUrl = fotoUrl;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getFotoUrl() { return fotoUrl; }
}