package com.example.applayouts;

public class Item {
    private int id;
    private String nombre;
    private String descripcion;
    private String atributos;
    private double precioCompra;
    private double precioVenta;

    public Item(int id, String nombre, String descripcion, String atributos, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.atributos = atributos;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
    }
    public Item()
    {

    }

}
