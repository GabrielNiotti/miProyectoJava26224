package com.techlab.laperfu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techlab.laperfu.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstadoIgnoreCase(String estado);

    List<Pedido> findByMetodoPagoIgnoreCase(String metodoPago);

    List<Pedido> findByUsuarioEmailOrderByFechaDesc(String email);

    List<Pedido> findAllByOrderByFechaDesc();
}
