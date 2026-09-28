package com.techlab.pedidos;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.productos.Producto;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static Long contadorPedidoId = 0L;
    
    private Long id;
    private List<LineaPedido> lineas;
    private double costoTotal;

    public Pedido() {
        this.id = ++contadorPedidoId;
        this.lineas = new ArrayList<>();
        this.costoTotal = 0.0;
    }

    public Long getId() {
        return id;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    // Agrega un producto al pedido y actualiza el costo total acumulado
    
    public void agregarItem(Producto producto, int cantidad) throws StockInsuficienteException {
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException("❌ Stock insuficiente para " + producto.getNombre() 
                + ". Disponible: " + producto.getStock() + ", Solicitado: " + cantidad);
        }
        lineas.add(new LineaPedido(producto, cantidad));
        costoTotal += producto.getPrecio() * cantidad;
    }

    // Recorre las líneas del pedido y reduce efectivamente el stock en el inventario
    public void confirmarPedido() {
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            p.setStock(p.getStock() - linea.getCantidad());
        }
    }

    // Muestra el detalle completo del pedido en consola
    public void mostrarDetallePedido() {
        System.out.println("\n=================================");
        System.out.println("PEDIDO ID: " + id);
        System.out.println("=================================");
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            System.out.printf("- %s x%d | Subtotal: $%.2f\n", 
                p.getNombre(), linea.getCantidad(), linea.getSubtotal());
        }
        System.out.println("---------------------------------");
        System.out.printf("COSTO TOTAL DEL PEDIDO: $%.2f\n", costoTotal);
        System.out.println("=================================");
    }

    //setter
    public void setCostoTotal (double costoTotal) {
        this.costoTotal = costoTotal;
    }
}
