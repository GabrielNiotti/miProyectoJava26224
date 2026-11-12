package com.techlab.laperfu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techlab.laperfu.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // 1. Filtra los pedidos por su estado actual (ignorando mayúsculas/minúsculas)
    List<Pedido> findByEstadoIgnoreCase(String estado);

    // 2. Filtra los pedidos según cómo pagó el cliente (ej: "efectivo", "tarjeta")
    List<Pedido> findByMetodoPagoIgnoreCase(String metodoPago);
}
