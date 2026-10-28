package com.techlab.productos;

import com.techlab.excepciones.PrecioInvalidoException;
import com.techlab.excepciones.StockInvalidoException;

public abstract class Producto {

    public static Long contadorId = 0L;
    private static int totalProductos = 0;

    private Long id;
    private String nombre;
    private Double precio;
    private int stock;

    public Producto (String nombre, Double precio, int stock){
        if (precio == null || precio <= 0) {
            throw new PrecioInvalidoException("El precio debe ser mayor a cero.");
        }
        if (stock < 0) {
            throw new StockInvalidoException(" El stock inicial no puede ser negativo.");
        }
        this.id = ++contadorId;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        totalProductos++;
    }     

    public abstract String getCategoria();

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    
    public void setPrecio(Double precio) {
        if (precio == null || precio <= 0) {
            throw new PrecioInvalidoException(" El nuevo precio debe ser mayor a cero.");
        }
        this.precio = precio;
    }

    public int getStock() { return stock; }
    
    public void setStock(int stock) {
        if (stock < 0) {
            throw new StockInvalidoException(" El stock modificado no puede ser negativo.");
        }
        this.stock = stock;
    }

    public static int getTotalProductos(){ return totalProductos; }

    public void mostrarDatos() {
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Stock: " + stock);
    }

    public abstract void aplicarDescuento(Double porcentaje);

    public void setId(int contadorId) {
        this.id = id;
    }

    
}
