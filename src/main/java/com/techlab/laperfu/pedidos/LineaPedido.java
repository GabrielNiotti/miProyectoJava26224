package com.techlab.laperfu.pedidos;

import com.techlab.laperfu.model.Producto;

public class LineaPedido {
    private Producto producto;
    private int cantidad;

    public LineaPedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    // Calcula el subtotal
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}
