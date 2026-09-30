package com.techlab.talentotech;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Crema;
import com.techlab.productos.Etiquetables;
import com.techlab.productos.Perfume;
import com.techlab.productos.Producto;
import com.techlab.productos.Shampoo;
import java.util.*;

public class menuConsola {
    static List<Producto> productos = new ArrayList<>();
    static List<Pedido> pedidos = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
            
        do {
            System.out.println("\n===== MENÚ =====");
            System.out.println("1 - Agregar Producto");
            System.out.println("2 - Modificar Producto");
            System.out.println("3 - Buscar Producto");
            System.out.println("4 - Eliminar Producto");
            System.out.println("5 - Listar productos");
            System.out.println("6 - Crear Pedido");
            System.out.println("7 - Listar Pedidos Realizados");
            System.out.println("8 - Salir");

            System.out.print("Seleccione una opción: ");

            try {
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) {
                    opcion = 0;
                    continue;
                }
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println(" Error: Debe ingresar un número entero válido.");
                opcion = 0; 
                continue;
            }
                
            switch (opcion) {
                case 1:
                    agregarProducto(scanner);
                    break;
                case 2:
                    modificarProducto(scanner);
                    break;
                case 3:
                    buscarProducto(scanner);
                    break;
                case 4:
                    eliminarProducto(scanner);
                    break;
                case 5:
                    listarProductos();
                    break;
                case 6:
                    crearPedido(scanner);
                    break;
                case 7:
                    listarPedidos();
                    break;    
                case 8:
                    System.out.println("Saliendo del programa...");
                    break;    
                default:
                    System.out.println("Opción incorrecta.");
            }

        } while (opcion != 8);

        scanner.close();
    }

    // agregar

    public static void agregarProducto(Scanner scanner) {
        int tipo = 0;
        do {
            System.out.println("\n--- AGREGAR PRODUCTO ---");
            System.out.println("1 - Perfume");
            System.out.println("2 - Crema");
            System.out.println("3 - Shampoo");
            System.out.print("Seleccione el tipo de producto: ");

            try {
                tipo = Integer.parseInt(scanner.nextLine().trim());
                if (tipo < 1 || tipo > 3) {
                    System.out.println(" Opción incorrecta. Debe elegir 1, 2 o 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Debe ingresar un número: 1, 2 o 3.");
                tipo = 0;
            }
        } while (tipo < 1 || tipo > 3);

        System.out.print("Ingrese nombre: ");
        String nombre = scanner.nextLine().toUpperCase();

        Double precio = 0.0;
        int stock = 0;

        try {
            System.out.print("Ingrese precio: ");
            precio = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Ingrese stock: ");
            stock = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(" Error de formato: Se ingresaron letras en vez de números. Producto no guardado.");
            return;
        }

        Producto producto = null;

        switch (tipo) {
            case 1:
                try {
                    System.out.print("Ingrese tamaño del envase (ml): ");
                    int mililitros = Integer.parseInt(scanner.nextLine().trim());
                    producto = new Perfume(nombre, precio, stock, mililitros);
                } catch (NumberFormatException e) {
                    System.out.println("Tamaño inválido. Operación cancelada.");
                    return;
                }
                break;
            case 2:
                System.out.print("Ingrese tipo de piel: ");
                String tipoPiel = scanner.nextLine().toUpperCase();
                producto = new Crema(nombre, precio, stock, tipoPiel);
                break;
            case 3:
                System.out.print("Ingrese tipo de cabello: ");
                String tipoCabello = scanner.nextLine().toUpperCase();
                producto = new Shampoo(nombre, precio, stock, tipoCabello);
                break;
        }

        productos.add(producto);
        System.out.println("✅ Producto agregado correctamente.");
        System.out.println("ID asignado: " + producto.getId());
    }

    // listar

        public static void listarProductos() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }

        for (Producto producto : productos) {
            System.out.println("-------------------------");
            System.out.println("ID: " + producto.getId());
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Precio: $" + producto.getPrecio());
            System.out.println("Stock: " + producto.getStock());
            System.out.println("Categoría: " + producto.getCategoria());

            if (producto instanceof Etiquetables) {
                ((Etiquetables) producto).generarEtiqueta();
            }
        }
        System.out.println("-------------------------");
    }

    // mostrar

    public static void mostrarProducto(Producto producto) {
        System.out.println("\n--- PRODUCTO ENCONTRADO ---");
        System.out.println("ID: " + producto.getId());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: $" + producto.getPrecio());
        System.out.println("Stock: " + producto.getStock());
        System.out.println("Categoría: " + producto.getCategoria());
        if (producto instanceof Etiquetables) {
            ((Etiquetables) producto).generarEtiqueta();
        }
    }

    //buscar

    public static void buscarProducto(Scanner scanner) {
        System.out.println("\n--- BUSCAR PRODUCTO ---");
        System.out.println("1 - Buscar por ID");
        System.out.println("2 - Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            if (opcion == 1) {
                System.out.print("Ingrese el ID del producto: ");
                Long id = Long.parseLong(scanner.nextLine().trim());

                for (Producto producto : productos) {
                    if (producto.getId().equals(id)) {
                        mostrarProducto(producto);
                        return;
                    }
                }
            } else if (opcion == 2) {
                System.out.print("Ingrese el nombre del producto: ");
                String nombre = scanner.nextLine().toUpperCase();

                for (Producto producto : productos) {
                    if (producto.getNombre().equals(nombre)) {
                        mostrarProducto(producto);
                        return;
                    }
                }
            } else {
                System.out.println("Opción incorrecta.");
                return;
            }
            System.out.println("No se encontró el producto.");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Ingrese caracteres numéricos.");
        }
    }

    // modificar

    public static void modificarProducto(Scanner scanner) {
        System.out.println("\n--- MODIFICAR PRODUCTO ---");
        try {
            System.out.print("Ingrese el ID del producto: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            for (Producto producto : productos) {
                if (producto.getId().equals(id)) {
                    System.out.println("\nProducto encontrado:");
                    mostrarProducto(producto);

                    System.out.println("\n¿Qué desea modificar?");
                    System.out.println("1 - Precio");
                    System.out.println("2 - Stock");
                    System.out.print("Seleccione una opción: ");

                    int opcion = Integer.parseInt(scanner.nextLine().trim());

                    switch (opcion) {
                        case 1 -> {
                            System.out.print("Ingrese el nuevo precio: ");
                            Double precio = Double.parseDouble(scanner.nextLine().trim());
                            if (precio < 0) {
                                System.out.println(" El precio no puede ser negativo.");
                                return;
                            }
                            producto.setPrecio(precio);
                            System.out.println("Precio actualizado correctamente.");
                        }
                        case 2 -> {
                            System.out.print("Ingrese el nuevo stock: ");
                            int stock = Integer.parseInt(scanner.nextLine().trim());
                            if (stock < 0) {
                                System.out.println("El stock no puede ser negativo.");
                                return;
                            }
                            producto.setStock(stock);
                            System.out.println("Stock actualizado correctamente.");
                        }
                        default -> System.out.println("Opción incorrecta.");
                    }
                    return;
                }
            }
            System.out.println("No se encontró un producto con ese ID.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Formato de número inválido.");
        }
    }

    // eliminar

    public static void eliminarProducto(Scanner scanner) {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        try {
            System.out.print("Ingrese el ID del producto: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            Iterator<Producto> iterator = productos.iterator();
            while (iterator.hasNext()) {
                Producto producto = iterator.next();
                if (producto.getId().equals(id)) {
                    iterator.remove();
                    System.out.println(" Producto eliminado correctamente.");
                    return;
                }
            }
            System.out.println("No se encontró un producto con ese ID.");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    //crear pedido

        public static void crearPedido(Scanner scanner) {
        System.out.println("\n--- CREAR NUEVO PEDIDO ---");
        if (productos.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
            return;
        }

        Pedido nuevoPedido = new Pedido();
        boolean agregando = true;

        while (agregando) {
            try {
                System.out.print("Ingrese el ID del producto que desea añadir: ");
                Long idProd = Long.parseLong(scanner.nextLine().trim());

                Producto productoEncontrado = null;
                for (Producto p : productos) {
                    if (p.getId().equals(idProd)) {
                        productoEncontrado = p;
                        break;
                    }
                }

                if (productoEncontrado == null) {
                    System.out.println(" Producto no encontrado.");
                } else {
                    System.out.print("Ingrese la cantidad deseada: ");
                    int cantidad = Integer.parseInt(scanner.nextLine().trim());

                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe ser mayor a cero.");
                    } else {
                        nuevoPedido.agregarItem(productoEncontrado, cantidad);
                        System.out.println("Producto añadido al pedido temporal.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Debe ingresar un valor numérico válido.");
            } catch (StockInsuficienteException e) {
                System.out.println(e.getMessage());
            }

            System.out.print("\n¿Desea agregar otro producto al pedido? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();
            if (!respuesta.equals("S")) {
                agregando = false;
            }
        }

        if (nuevoPedido.getLineas().isEmpty()) {
            System.out.println("Pedido cancelado porque no contiene productos.");
            return;
        }

        // --- LÓGICA DE SELECCIÓN DE MÉTODO DE PAGO Y DESCUENTO ---
        double totalOriginal = nuevoPedido.getCostoTotal();
        double totalConDescuento = totalOriginal;
        String metodoPago = "";

        System.out.println("\n--- SELECCIONE MÉTODO DE PAGO ---");
        System.out.println("1 - Efectivo (10% de descuento 🎉)");
        System.out.println("2 - Tarjeta / Debito (Precio de lista)");
        System.out.print("Seleccione una opción: ");
        
        try {
            int opcionPago = Integer.parseInt(scanner.nextLine().trim());
            if (opcionPago == 1) {
                metodoPago = "EFECTIVO";
                // Calculamos el 10% de descuento
                double descuento = totalOriginal * 0.10;
                totalConDescuento = totalOriginal - descuento;
                System.out.printf("¡Se aplicó un 10%% de descuento por pago en efectivo! Ahorro: $%.2f\n", descuento);
            } else {
                metodoPago = "TARJETA/DEBITO";
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Opción inválida. Se procesará con precio de lista (Tarjeta).");
            metodoPago = "TARJETA/DEBITO";
        }

        System.out.println("\n--- RESUMEN FINAL DEL PEDIDO ---");
        nuevoPedido.mostrarDetallePedido();
        if (metodoPago.equals("EFECTIVO")) {
            System.out.printf("Forma de pago: %s\n", metodoPago);
            System.out.printf("TOTAL NETO A PAGAR: $%.2f\n", totalConDescuento);
            System.out.println("=================================");
        }

        System.out.print("¿Confirma el pedido? El stock se descontará (S/N): ");
        String confirmar = scanner.nextLine().trim().toUpperCase();

        if (confirmar.equals("S")) {
            nuevoPedido.confirmarPedido();
            pedidos.add(nuevoPedido);
            System.out.println("🎉 ¡Pedido #" + nuevoPedido.getId() + " confirmado con éxito!");
        } else {
            System.out.println("❌ Pedido descartado.");
        }
    }

    // listado de pedido


    public static void listarPedidos() {
        System.out.println("\n--- LISTA DE PEDIDOS REALIZADOS ---");
        if (pedidos.isEmpty()) {
            System.out.println("No se ha realizado ningún pedido aún.");
            return;
        }

        for (Pedido p : pedidos) {
            p.mostrarDetallePedido();
        }
    }
}

