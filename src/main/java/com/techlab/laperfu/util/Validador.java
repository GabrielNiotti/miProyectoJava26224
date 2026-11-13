package com.techlab.laperfu.util;

import java.math.BigDecimal;

import com.techlab.laperfu.exception.StockInsuficienteException;
import com.techlab.laperfu.model.Categoria;

public class Validador {

    public static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
    }

     public static void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio es obligatorio y no puede ser negativo.");
        }
 
    }

    public static void validarStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new StockInsuficienteException("El stock no puede ser negativo.");
        }
    }

    public static void validarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Debe asignar una categoría válida con su ID.");
        }
    }
}
