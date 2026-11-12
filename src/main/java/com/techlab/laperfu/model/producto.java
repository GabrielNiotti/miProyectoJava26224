package com.techlab.laperfu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (columnDefinition = "INT")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio; 

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String descripcion; 

    @Column (length = 255)
    private String img;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Constructor Vacío (Requerido por JPA)
    public Producto() {
    }

    // Constructor Completo
    public Producto(Long id, String nombre, BigDecimal precio, Integer stock, String descripcion, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    // Getters y Setters Manuales (Para evitar el procesador fallido)
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public BigDecimal getPrecio() { 
        return precio; 
    }
    public void setPrecio(BigDecimal precio) { 
        this.precio = precio; 
    }

    public Integer getStock() { 
        return stock; 
    }
    public void setStock(Integer stock) { 
        this.stock = stock; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public String getImg() { 
        return img; 
    }

    public void setImg(String img) { 
        this.img = img; 
    }

    public Categoria getCategoria() { 
        return categoria; 
    }
    public void setCategoria(Categoria categoria) { 
        this.categoria = categoria; 
    }
}
