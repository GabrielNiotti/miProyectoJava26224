package com.techlab.util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Validador {

    public static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); // Limpia el buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("❌ Debe ingresar un número entero. Intente nuevamente.");
                scanner.nextLine(); // Limpia el buffer
            }
        }
    }

    public static double leerDoble(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = scanner.nextDouble();
                scanner.nextLine(); // Limpia el buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("❌ Debe ingresar un número decimal. Intente nuevamente.");
                scanner.nextLine(); // Limpia el buffer
            }
        }
    }

    public static String leerTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }
}