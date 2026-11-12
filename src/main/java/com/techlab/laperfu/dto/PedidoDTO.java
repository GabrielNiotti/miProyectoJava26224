package com.techlab.laperfu.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoDTO {
    private BigDecimal total;
    private List<ItemCarritoDTO> items;

    // Getters y Setters
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<ItemCarritoDTO> getItems() { return items; }
    public void setItems(List<ItemCarritoDTO> items) { this.items = items; }

    public static class ItemCarritoDTO {
        private Long id;
        private Integer cantidad;
        private BigDecimal precio;

        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
    }

// Formas de pago y cuotas:
private String metodoPago;
private Integer cuotas;

// SUS GETTERS Y SETTERS:
public String getMetodoPago() { return metodoPago; }
public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
public Integer getCuotas() { return cuotas; }
public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }

}