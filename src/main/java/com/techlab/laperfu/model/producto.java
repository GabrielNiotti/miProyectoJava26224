package com.techlab.laperfu.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techlab.laperfu.exception.PrecioInvalidoException;
import com.techlab.laperfu.exception.StockInvalidoException;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@JsonPropertyOrder({ "id", "nombre", "precio", "stock", "categoria"})
@NoArgsConstructor(access = AccessLevel.PUBLIC) // Exigido por JPA y Jackson
@Entity
@Table(name = "productos")
public class Producto implements Vendible, Etiquetables {

    @Transient 
    @Getter 
    private static int totalProductos = 0;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Getter 
    private Long id;
    
    @Getter @Setter 
    private String nombre;
    
    @Getter 
    private Double precio;
    
    @Getter 
    private int stock;

    @Getter @Setter 
    private String categoria; // 👈 Aquí guardaremos "crema", "perfume" o "shampoo"

    // Constructor con parámetros
    public Producto (String nombre, Double precio, int stock, String categoria){
        if (precio == null || precio <= 0) {
            throw new PrecioInvalidoException("El precio debe ser mayor a cero.");
        }
        if (stock < 0) {
            throw new StockInvalidoException("El stock inicial no puede ser negativo.");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        totalProductos++;
    }     
    
    // Implementacion interfaz Vendible
    @Override 
    public void aplicarDescuento(Double porcentaje) {
        if (porcentaje != null && porcentaje > 0 && porcentaje <= 100) {
            this.precio -= this.precio * (porcentaje / 100);
        }
    }

    // Implementacion metodo intervaz Etiquetables

    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta para: " + this.nombre + "[" + this.categoria + "]");
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrecio(Double precio) {
        if (precio == null || precio <= 0) {
            throw new PrecioInvalidoException("El nuevo precio debe ser mayor a cero.");
        }
        this.precio = precio;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new StockInvalidoException("El stock modificado no puede ser negativo.");
        }
        this.stock = stock;
    }

    public void mostrarDatos() {
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Stock: " + stock);
        System.out.println("Categoría: " + categoria);
    }
}

