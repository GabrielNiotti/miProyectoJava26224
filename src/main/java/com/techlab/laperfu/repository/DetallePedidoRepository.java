package com.techlab.laperfu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techlab.laperfu.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    // Obtiene todos los renglones de un pedido específico
    List<DetallePedido> findByPedidoId(Long pedidoId);

    // Verifica si algún pedido histórico incluye este producto antes de eliminarlo
    boolean existsByProductoId(Long productoId);
}
