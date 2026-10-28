package com.example.persistencia;

public class Producto {
    public int id;
    public String producto;
    public String descripcion;
    public int precio;

    public Producto(int id, String producto, String descripcion, int precio) {
        this.id = id;
        this.producto = producto;
        this.descripcion = descripcion;
        this.precio = precio;
    }
}