package com.techlab.laperfu.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;

@Schema(description = "Entidad que representa un perfume o producto de la perfumería")
@Entity
@Table(name = "producto")
public class Producto {

    @Schema(description = "Identificador único generado por la base de datos", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT")
    private Long id;

    @Schema(description = "Nombre comercial de la fragancia y presentación", example = "La Vie Est Belle EDP 100ml")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Precio de venta en pesos", example = "145000.00")
    @Column(nullable = false)
    private BigDecimal precio; 

    @Schema(description = "Cantidad de unidades disponibles en inventario", example = "15")
    @Column(nullable = false)
    private Integer stock;

    @Schema(description = "Notas olfativas y descripción detallada de la fragancia", example = "Fragancia floral dulce con notas de iris, jazmín, flor de azahar y pachulí.")
    @Column(columnDefinition = "TEXT")
    private String descripcion; 

    @Schema(description = "URL o ruta de la imagen del producto", example = "https://ejemplo.com")
    @Column(length = 255)
    private String img;

    @Schema(description = "Categoría a la que pertenece el producto (ej. Femenino, Masculino, Unisex)")
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

    // Getters y Setters Manuales
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
