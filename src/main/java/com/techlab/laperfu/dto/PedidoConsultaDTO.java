package com.techlab.laperfu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoConsultaDTO {

    private Long id;
    private LocalDateTime fecha;
    private String metodoPago;
    private Integer cuotas;
    private BigDecimal total;
    private String estado;
    private Integer cantidadProductos;

    public PedidoConsultaDTO() {
    }

    public PedidoConsultaDTO(Long id, LocalDateTime fecha, String metodoPago, Integer cuotas, BigDecimal total, String estado, Integer cantidadProductos) {
        this.id = id;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.cuotas = cuotas;
        this.total = total;
        this.estado = estado;
        this.cantidadProductos = cantidadProductos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getCantidadProductos() { return cantidadProductos; }
    public void setCantidadProductos(Integer cantidadProductos) { this.cantidadProductos = cantidadProductos; }
}

