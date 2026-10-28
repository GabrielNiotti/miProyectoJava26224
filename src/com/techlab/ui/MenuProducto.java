package com.techlab.ui;

import com.techlab.productos.*;
import com.techlab.service.ProductoService;
import com.techlab.util.Validador;
import java.util.*;

public class MenuProducto {
    
    private final Scanner sc;
    private final ProductoService service;

    // Constructor que recibe las dependencias (Patrón de Inyección)
    public MenuProducto(Scanner sc, ProductoService service) {
        this.sc = sc;
        this.service = service;
    }

    public void mostrarMenu() {
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
    }

    public void agregarProducto() {
        int tipo = 0;
        do {
            System.out.println("\n--- AGREGAR PRODUCTO ---");
            System.out.println("1 - Perfume");
            System.out.println("2 - Crema");
            System.out.println("3 - Shampoo");
            System.out.print("Seleccione el tipo de producto: ");

            try {
                tipo = Integer.parseInt(sc.nextLine().trim());
                if (tipo < 1 || tipo > 3) {
                    System.out.println(" Opción incorrecta. Debe elegir 1, 2 o 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Debe ingresar un número: 1, 2 o 3.");
                tipo = 0;
            }
        } while (tipo < 1 || tipo > 3);

        // Usamos los métodos del Validador que lee directamente del Scanner inyectado
        String nombre = Validador.leerTexto(sc, "Ingrese nombre: ").toUpperCase();
        double precio = Validador.leerDouble(sc, "Ingrese precio: ");
        int stock = Validador.leerEntero(sc, "Ingrese stock: ");

        Producto producto = null;

        switch (tipo) {
            case 1 -> {
                String genero = "";
                do {
                    System.out.print("Ingrese género para el perfume (HOMBRE / MUJER): ");
                    genero = sc.nextLine().trim().toUpperCase();
                    if (!genero.equals("HOMBRE") && !genero.equals("MUJER")) {
                        System.out.println("❌ Opción inválida. Escriba HOMBRE o MUJER.");
                    }
                } while (!genero.equals("HOMBRE") && !genero.equals("MUJER"));
                
                producto = new Perfume(nombre, precio, stock, genero);
            }
            case 2 -> {
                String tipoPiel = Validador.leerTexto(sc, "Ingrese tipo de piel: ").toUpperCase();
                producto = new Crema(nombre, precio, stock, tipoPiel);
            }
            case 3 -> {
                String tipoCabello = Validador.leerTexto(sc, "Ingrese tipo de cabello: ").toUpperCase();
                producto = new Shampoo(nombre, precio, stock, tipoCabello);
            }
        }

        if (producto != null) {
            Producto guardado = service.guardar(producto);
            System.out.println("  Producto agregado correctamente.");
            System.out.println("ID asignado: " + guardado.getId());
        }
    }

    public void listarProductos() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        List<Producto> lista = service.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }

        for (Producto producto : lista) {
            imprimirDetalleProducto(producto);
        }
    }

    // BUSCAR PROCUCTO
    public void buscarProducto() {
        System.out.println("\n--- BUSCAR PRODUCTO ---");
        int id = Validador.leerEntero(sc, "Ingrese el ID del producto a buscar: ");
        
        // El servicio arrojará ProductoNoEncontradoException si no existe, la cual se maneja en Main
        Producto producto = service.obtenerPorId(id); 
        
        System.out.println("\n--- PRODUCTO ENCONTRADO ---");
        imprimirDetalleProducto(producto);
    }

    // Método utilitario interno para reutilizar la lógica de impresión y etiquetas
    private void imprimirDetalleProducto(Producto producto) {
        System.out.println("-------------------------");
        System.out.println("ID: " + producto.getId());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: $" + producto.getPrecio());
        System.out.println("Stock: " + producto.getStock());
        System.out.println("Categoría: " + producto.getCategoria());

        if (producto instanceof Etiquetables) {
            ((Etiquetables) producto).generarEtiqueta();
        }
        System.out.println("-------------------------");
    }

        // --- MODIFICAR PRODUCTO ---
    /**
     * 
     */
    public void modificarProducto() {
    System.out.println("\n--- MODIFICAR PRODUCTO ---");
    int id = Validador.leerEntero(sc, "Ingrese el ID del producto a modificar: ");

    // CORRECCIÓN 1: Cambiar 'buscarPorId' por tu método real 'obtenerPorId'
    Producto productoExistente = service.obtenerPorId(id);

    System.out.println("\nProducto seleccionado: " + productoExistente.getNombre());
    System.out.println("Deje en blanco o presione Enter si no desea modificar un campo de texto.");

    // 1. Modificar Nombre
    System.out.print("Nuevo nombre [" + productoExistente.getNombre() + "]: ");
    String nuevoNombre = sc.nextLine().trim().toUpperCase();
    if (!nuevoNombre.isEmpty()) {
        productoExistente.setNombre(nuevoNombre);
    }

    // 2. Modificar Precio
    System.out.print("Nuevo precio [$" + productoExistente.getPrecio() + "]: ");
    String entradaPrecio = sc.nextLine().trim();
    if (!entradaPrecio.isEmpty()) {
        try {
            double nuevoPrecio = Double.parseDouble(entradaPrecio);
            productoExistente.setPrecio(nuevoPrecio);
        } catch (NumberFormatException e) {
            System.out.println("❌ Formato de precio inválido. No se modificó este campo.");
        }
    }

    // 3. Modificar Stock
    System.out.print("Nuevo stock [" + productoExistente.getStock() + "]: ");
    String entradaStock = sc.nextLine().trim();
    if (!entradaStock.isEmpty()) {
        try {
            int nuevoStock = Integer.parseInt(entradaStock);
            productoExistente.setStock(nuevoStock);
        } catch (NumberFormatException e) {
            System.out.println("❌ Formato de stock inválido. No se modificó este campo.");
        }
    }

    // 4. Modificar atributos específicos según la subclase polimórfica
    if (productoExistente instanceof Perfume perfume) {
        System.out.print("Nuevo género [" + perfume.getGenero() + "] (HOMBRE/MUJER o Enter para omitir): ");
        String nuevoGenero = sc.nextLine().trim().toUpperCase();
        if (!nuevoGenero.isEmpty()) {
            if (nuevoGenero.equals("HOMBRE") || nuevoGenero.equals("MUJER")) {
                perfume.setGenero(nuevoGenero);
            } else {
                System.out.println("❌ Género no válido. No se modificó este campo.");
            }
        }
    } else if (productoExistente instanceof Crema crema) {
        System.out.print("Nuevo tipo de piel [" + crema.getTipoPiel() + "]: ");
        String nuevoTipoPiel = sc.nextLine().trim().toUpperCase();
        if (!nuevoTipoPiel.isEmpty()) {
            crema.setTipoPiel(nuevoTipoPiel);
        }
    } else if (productoExistente instanceof Shampoo shampoo) {
        System.out.print("Nuevo tipo de cabello [" + shampoo.getTipoCabello() + "]: ");
        String nuevoTipoCabello = sc.nextLine().trim().toUpperCase();
        if (!nuevoTipoCabello.isEmpty()) {
            shampoo.setTipoCabello(nuevoTipoCabello);
        }
    }
    
    // CORRECCIÓN 2: Le pasamos los dos parámetros requeridos por la firma (el ID y el objeto modificado)
    service.actualizar(id, productoExistente);
    
    System.out.println(" Producto modificado correctamente.");
}


    // --- ELIMINAR PRODUCTO ---
    public void eliminarProducto() {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        int id = Validador.leerEntero(sc, "Ingrese el ID del producto a eliminar: ");

        // Buscamos el producto para mostrar confirmación al usuario
        Producto producto = service.obtenerPorId(id);

        System.out.println("\n ¿Está seguro que desea eliminar el producto '" + producto.getNombre() + "'? (S/N): ");
        String confirmacion = sc.nextLine().trim().toUpperCase();

        if (confirmacion.equals("S")) {
            service.eliminar(id);
            System.out.println(" Producto eliminado con éxito.");
        } else {
            System.out.println(" Operación cancelada. El producto no fue eliminado.");
        }
    }

}
