package com.techlab.util;

import com.techlab.excepciones.StockInvalidoException; // Importa tu excepción personalizada
import java.util.Scanner;

/*
    Clase con métodos de validación reutilizables.
    Todos los métodos son estáticos: no necesitamos crear una instancia de Validador para usarlos.
    Se invocan directamente.
*/
public class Validador {

    // ==========================================
    // VALIDACIONES DE NEGOCIO (Lanzan excepciones)
    // ==========================================

    public static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
    }

    public static void validarPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
    }

    public static void validarStock(int stock) {
        // Vinculado con tu clase StockInvalidoException
        if (stock < 0) {
            throw new StockInvalidoException("El stock no puede ser negativo.");
        }
    }

    public static void validarCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }
    }

    // ==========================================
    // MÉTODOS DE LECTURA DE CONSOLA (UI)
    // ==========================================

    public static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje); // Cambiado a print para escribir al lado del texto
            try {
                // Lee la línea completa como texto y la convierte, evitando errores de buffer
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Debe ingresar un número entero válido. Intente nuevamente.\n");
            }
        }
    }

    public static double leerDouble(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje); // Cambiado a print para escribir al lado del texto
            try {
                // Lee la línea completa y la convierte a decimal de forma segura
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Debe ingresar un número decimal válido (ej: 15.50). Intente nuevamente.\n");
            }
        }
    }

    public static String leerTexto(Scanner sc, String mensaje) {
        System.out.print(mensaje); // Cambiado a print para mejor experiencia visual
        return sc.nextLine();
    }
}
