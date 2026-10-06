package com.techlab.talentotech;

import com.techlab.excepciones.*;
import com.techlab.pedidos.*;
import com.techlab.productos.*;
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

            try {
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
            } catch (PrecioInvalidoException e) {
                System.out.println("\n Error de Validación (Precio): " + e.getMessage());
            } catch (StockInvalidoException e) {
                System.out.println("\n Error de Validación (Stock): " + e.getMessage());
            } catch (StockInsuficienteException e) {
                System.out.println("\n Error en Pedido (Inventario): " + e.getMessage());
            } catch (ProductoNoEncontradoException e) {
                System.out.println("\n Error de Búsqueda: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\n Error de Entrada: " + e.getMessage());
            }

        } while (opcion != 8);

        scanner.close();
    }

    // --- AGREGAR PRODUCTO ---
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

        if (producto != null) {
            productos.add(producto);
            System.out.println(" Producto agregado correctamente.");
            System.out.println("ID asignado: " + producto.getId());
        }
    }

    // --- LISTAR PRODUCTOS ---
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

    // --- MOSTRAR PRODUCTO ---
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

    // --- BUSCAR PRODUCTO ---
    public static void buscarProducto(Scanner scanner) {
        System.out.println("\n--- BUSCAR PRODUCTO ---");
        System.out.println("1 - Buscar por ID");
        System.out.println("2 - Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcionBuscador = Integer.parseInt(scanner.nextLine().trim());
            if (opcionBuscador == 1) {
                System.out.print("Ingrese el ID del producto: ");
                Long id = Long.parseLong(scanner.nextLine().trim());

                for (Producto producto : productos) {
                    if (producto.getId().equals(id)) {
                        mostrarProducto(producto);
                        return;
                    }
                }
                throw new ProductoNoEncontradoException("No se encontró ningún producto con el ID: " + id);

            } else if (opcionBuscador == 2) {
                System.out.print("Ingrese el nombre del producto: ");
                String nombre = scanner.nextLine().toUpperCase();

                for (Producto producto : productos) {
                    if (producto.getNombre().equals(nombre)) {
                        mostrarProducto(producto);
                        return;
                    }
                }
                throw new ProductoNoEncontradoException("No se encontró ningún producto con el nombre: " + nombre);
            } else {
                System.out.println("Opción incorrecta.");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Entrada inválida. Ingrese caracteres numéricos.");
        }
    }

        // --- MODIFICAR PRODUCTO (ACTUALIZADO: PERMITE MODIFICAR EL NOMBRE) ---
    public static void modificarProducto(Scanner scanner) {
        System.out.println("\n--- MODIFICAR PRODUCTO ---");
        try {
            System.out.print("Ingrese el ID del producto a modificar: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            Producto productoEncontrado = null;
            for (Producto p : productos) {
                if (p.getId().equals(id)) {
                    productoEncontrado = p;
                    break;
                }
            }

            if (productoEncontrado == null) {
                throw new ProductoNoEncontradoException("No se puede modificar: El ID no existe.");
            }

            System.out.println("Producto actual: " + productoEncontrado.getNombre());
            
            // 1. NUEVA SECCIÓN: MODIFICAR NOMBRE
            System.out.print("Ingrese nuevo nombre (Vacío para mantener '" + productoEncontrado.getNombre() + "'): ");
            String nuevoNombre = scanner.nextLine().trim().toUpperCase();
            if (!nuevoNombre.isEmpty()) {
                productoEncontrado.setNombre(nuevoNombre);
            }

            // 2. MODIFICAR PRECIO
            System.out.print("Ingrese nuevo precio (Vacío para mantener $" + productoEncontrado.getPrecio() + "): ");
            String nuevoPrecioStr = scanner.nextLine().trim();
            if (!nuevoPrecioStr.isEmpty()) {
                productoEncontrado.setPrecio(Double.parseDouble(nuevoPrecioStr));
            }

            // 3. MODIFICAR STOCK
            System.out.print("Ingrese nuevo stock (Vacío para mantener " + productoEncontrado.getStock() + " unidades): ");
            String nuevoStockStr = scanner.nextLine().trim();
            if (!nuevoStockStr.isEmpty()) {
                productoEncontrado.setStock(Integer.parseInt(nuevoStockStr));
            }

            System.out.println(" Producto modificado exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println(" Error: Formato numérico ingresado inválido.");
        }
    }

// --- ELIMINAR PRODUCTO ---

    public static void eliminarProducto(Scanner scanner) {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        try {
            System.out.print("Ingrese el ID del producto a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            Producto productoEncontrado = null;
            for (Producto p : productos) {
                if (p.getId().equals(id)) {
                    productoEncontrado = p;
                    break;
                }
            }
            if (productoEncontrado == null) {
                throw new ProductoNoEncontradoException("No se puede eliminar: El ID no existe.");
            }
            productos.remove(productoEncontrado);
            System.out.println(" Producto '" + productoEncontrado.getNombre() + "' eliminado correctamente.");
        } catch (NumberFormatException e) {
            System.out.println(" Error: Ingrese un ID numérico válido.");
        }
    }
// --- CREAR PEDIDO ---

    public static void crearPedido(Scanner scanner) {
        System.out.println("\n--- CREAR PEDIDO ---");
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados en el inventario.");
            return;
        }
        Pedido nuevoPedido = new Pedido();
        boolean agregando = true;
        while (agregando) {
            listarProductos();
            try {
                System.out.print("Ingrese el ID del producto a agregar al pedido (0 para finalizar): ");
                Long id = Long.parseLong(scanner.nextLine().trim());
                if (id == 0) {
                    break;
                }
                Producto productoSeleccionado = null;
                for (Producto p : productos) {
                    if (p.getId().equals(id)) {
                        productoSeleccionado = p;
                        break;
                    }
                }
                if (productoSeleccionado == null) {
                    throw new ProductoNoEncontradoException("El ID ingresado no coincide con ningún producto.");
                }
                System.out.print("Ingrese la cantidad requerida: ");
                int cantidad = Integer.parseInt(scanner.nextLine().trim());
                nuevoPedido.agregarItem(productoSeleccionado, cantidad);
                System.out.println("🛒 Item agregado provisionalmente al carrito.");
                System.out.print("¿Desea ingresar otro producto a este pedido? (S/N): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                    agregando = false;
                }
            } catch (NumberFormatException e) {
                System.out.println(" Error: Ingrese números válidos para los identificadores y las cantidades.");
            }
        }
        if (!nuevoPedido.getLineas().isEmpty()) {
            int metodoPago = 0;
            do {
                System.out.println("\n--- SELECCIONE MÉTODO DE PAGO ---");
                System.out.println("1 - Efectivo (10% de Descuento)");
                System.out.println("2 - Tarjeta / Debito (Precio de lista)");
                System.out.print("Seleccione una opción: ");
                try {
                    metodoPago = Integer.parseInt(scanner.nextLine().trim());
                    if (metodoPago == 1) {
                        nuevoPedido.setTipoPago("EFECTIVO");
                    } else if (metodoPago == 2) {
                        nuevoPedido.setTipoPago("TARJETA");
                    } else {
                        System.out.println(" Opción incorrecta. Seleccione 1 o 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Error: Debe ingresar 1 o 2.");
                    metodoPago = 0;
                }
            } while (metodoPago != 1 && metodoPago != 2);
            nuevoPedido.confirmarPedido();
            System.out.println("\n--- RESUMEN FINAL DEL PEDIDO ---");
            nuevoPedido.mostrarDetallePedido();
            pedidos.add(nuevoPedido);
            System.out.println(" Pedido completado y registrado exitosamente.");
        } else {
            System.out.println(" Pedido cancelado: Carrito vacío.");
        }
    }
// --- LISTAR PEDIDOS ---

    public static void listarPedidos() {
        System.out.println("\n--- PEDIDOS REALIZADOS ---");
        if (pedidos.isEmpty()) {
            System.out.println("No se han registrado pedidos todavía.");
            return;
        }
        for (Pedido pedido : pedidos) {
            pedido.mostrarDetallePedido();
        }
    }
}
