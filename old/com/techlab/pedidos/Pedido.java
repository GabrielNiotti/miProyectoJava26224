package com.techlab.pedidos; // IMPORTANTE: Declara el paquete para que menuConsola lo encuentre

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.productos.Producto;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static Long contadorPedidoId = 0L;
    
    private Long id;
    private List<LineaPedido> lineas;
    private double costoTotal;
    private String tipoPago = "EFECTIVO"; // Controla el método de pago elegido

    public Pedido() {
        this.id = ++contadorPedidoId;
        this.lineas = new ArrayList<>();
        this.costoTotal = 0.0;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago.trim().toUpperCase();
    }

    public String getTipoPago() {
        return tipoPago;
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

    public void agregarItem(Producto producto, int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException("Stock insuficiente para " + producto.getNombre() 
                + ". Disponible: " + producto.getStock() + ", Solicitado: " + cantidad);
        }
        lineas.add(new LineaPedido(producto, cantidad));
        costoTotal += producto.getPrecio() * cantidad;
    }

    public void confirmarPedido() {
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            p.setStock(p.getStock() - linea.getCantidad());
        }
    }

    public void mostrarDetallePedido() {
        System.out.println("\n=================================");
        System.out.println("PEDIDO ID: " + id + " | FORMA DE PAGO: " + tipoPago);
        System.out.println("=================================");
        for (LineaPedido linea : lineas) {
            Producto p = linea.getProducto();
            System.out.printf("- %s | Precio Unitario: $%.2f x%d | Subtotal: $%.2f\n", 
                p.getNombre(), p.getPrecio(), linea.getCantidad(), linea.getSubtotal());
        }
        System.out.println("---------------------------------");
        System.out.printf("COSTO TOTAL:                $%.2f\n", costoTotal);
        
        double totalAPagar = costoTotal;

        if (tipoPago.equals("EFECTIVO")) {
            double porcentajeEfectivo = 10.0;
            double importeQueSeDescuenta = costoTotal * (porcentajeEfectivo / 100.0);
            totalAPagar = costoTotal - importeQueSeDescuenta;
            System.out.printf("IMPORTE QUE SE DESCUENTA:  -$%.2f (10%% Efectivo)\n", importeQueSeDescuenta);
        } else {
            System.out.println("IMPORTE QUE SE DESCUENTA:   $0.00 (Sin descuento con Tarjeta)");
        }

        System.out.println("---------------------------------");
        System.out.printf("TOTAL A PAGAR:              $%.2f\n", totalAPagar);
        System.out.println("=================================");
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }
}
