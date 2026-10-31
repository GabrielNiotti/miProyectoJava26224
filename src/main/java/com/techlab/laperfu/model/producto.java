package com.techlab.laperfu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto") // Le avisa a Java que use tu tabla existente
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mapea tu INT AUTO_INCREMENT

    private String nombre;
    private Double precio; // Mapea tu DECIMAL(10,2)
    private Integer stock;

    @Column(name = "categoria_id") // Mapea exactamente tu columna de XAMPP
    private Long categoriaId;

    private String descripcion; // Volvemos a 'descripcion' como pusiste en tu CREATE TABLE

    public Producto() {}

    // --- GETTERS Y SETTERS PERFECTOS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}
