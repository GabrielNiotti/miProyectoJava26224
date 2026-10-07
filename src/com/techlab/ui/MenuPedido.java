package com.techlab.ui;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.service.PedidoService;
import com.techlab.service.ProductoService;
import com.techlab.util.Validador;
import java.util.Scanner;

public class MenuPedido {
    private final Scanner sc;
    private final ProductoService productoService;
    private final PedidoService pedidoService;

    public MenuPedido(Scanner sc, ProductoService productoService, PedidoService pedidoService) {
        this.sc = sc;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
    }

        // --- CREAR PEDIDO ---
    public void crearPedido() throws StockInsuficienteException {
        System.out.println("\n===== CREAR NUEVO PEDIDO =====");
        Pedido pedido = new Pedido();
        boolean agregando = true;

        while (agregando) {
            int idProducto = Validador.leerEntero(sc, "Ingrese el ID del producto que desea comprar: ");
            
            // CORREGIDO: Se cambió 'PorId' por tu método real 'obtenerPorId' 🛒
            Producto producto = productoService.obtenerPorId(idProducto); 
            
            System.out.println("Producto seleccionado: " + producto.getNombre() + " (Stock actual: " + producto.getStock() + ")");
            int cantidad = Validador.leerEntero(sc, "Ingrese la cantidad a comprar: ");

            // Agrega el ítem y calcula subtotales intermedios. Valida stock internamente en Pedido
            pedido.agregarItem(producto, cantidad);
            System.out.println(" Ítem agregado al carrito.");

            System.out.print("¿Desea agregar otro producto a este pedido? (S/N): ");
            String continuar = sc.nextLine().trim().toUpperCase();
            if (!continuar.equals("S")) {
                agregando = false;
            }
        }

        // Selección del método de pago
        System.out.println("\nSeleccione el método de pago:");
        System.out.println("1 - Efectivo (10% de descuento)");
        System.out.println("2 - Tarjeta (Sin descuento)");
        int opcionPago = Validador.leerEntero(sc, "Opción: ");
        
        if (opcionPago == 1) {
            pedido.setTipoPago("EFECTIVO");
        } else {
            pedido.setTipoPago("TARJETA");
        }

        // Mostrar resumen, aplicar descuentos e impactar stock
        pedido.mostrarDetallePedido();
        pedido.confirmarPedido();
        
        // Persistir en el historial del servicio
        pedidoService.registrarPedido(pedido);
        System.out.println("\n ¡Pedido confirmado y registrado con éxito!");
    }

        
    

    // --- LISTAR PEDIDOS REALIZADOS ---
    public void listarPedidos() {
        System.out.println("\n===== HISTORIAL DE PEDIDOS REALIZADOS =====");
        var lista = pedidoService.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("No se han registrado pedidos en el sistema.");
            return;
        }

        for (Pedido p : lista) {
            p.mostrarDetallePedido();
        }
    }
}
