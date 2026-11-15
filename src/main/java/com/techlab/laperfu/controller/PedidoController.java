package com.techlab.laperfu.controller;

import com.techlab.laperfu.dto.DetallePedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.model.DetallePedido;
import com.techlab.laperfu.model.Pedido;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.PedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            Pedido pedido = new Pedido();
            pedido.setTotal(pedidoDTO.getTotal());
            pedido.setMetodoPago(pedidoDTO.getMetodoPago());
            pedido.setCuotas(pedidoDTO.getCuotas());

            List<DetallePedido> detalles = new ArrayList<>();

            for (PedidoDTO.ItemCarritoDTO itemDTO : pedidoDTO.getItems()) {
                Producto producto = productoRepository.findById(itemDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.getId()));

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(itemDTO.getCantidad());
                detalle.setPrecioUnitario(itemDTO.getPrecio());

                detalles.add(detalle);
            }

            pedido.setDetalles(detalles);
            pedidoRepository.save(pedido);

            return ResponseEntity.ok(Map.of("status", "success", "mensaje", "¡Pedido guardado con éxito en MySQL!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "mensaje", e.getMessage()));
        }
    }

    // ENDPOINT PARA LISTAR PEDIDOS
    
    @GetMapping
    public ResponseEntity<List<PedidoConsultaDTO>> listarTodosLosPedidos() {
        try {
            List<Pedido> listaPedidos = pedidoRepository.findAll();
            List<PedidoConsultaDTO> respuesta = listaPedidos.stream()
                    .map(this::mapearADTO)
                    .toList();
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    // ENDPOINT PARA ACTUALIZAR EL ESTADO DEL PEDIDO (Maneja los 3 estados cíclicos)
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            String estadoUpper = nuevoEstado.toUpperCase();

            if (estadoUpper.equals("PENDIENTE") || 
                estadoUpper.equals("EN_PROCESO") || 
                estadoUpper.equals("REALIZADO") || 
                estadoUpper.equals("CANCELADO")) {
                
                pedido.setEstado(estadoUpper);
            } else {
                throw new IllegalArgumentException("Estado no válido. Use: PENDIENTE, EN_PROCESO o REALIZADO");
            }

            pedidoRepository.save(pedido);

            return ResponseEntity.ok(Map.of("status", "success", "mensaje", "Estado del pedido actualizado a: " + estadoUpper));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "mensaje", e.getMessage()));
        }
    }

    // CONSULTA 1: Filtrar pedidos por estado con detalle de productos
    // Ruta: GET http://localhost:8080/pedido/filtrar/estado?estado=PENDIENTE
    @GetMapping("/filtrar/estado")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorEstado(@RequestParam String estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstadoIgnoreCase(estado);
        List<PedidoConsultaDTO> respuesta = pedidos.stream()
                .map(this::mapearADTO)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // CONSULTA 2: Filtrar pedidos por método de pago con detalle de productos
    // Ruta: GET http://localhost:8080/pedido/filtrar/pago?metodo=tarjeta
    @GetMapping("/filtrar/pago")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorMetodoPago(@RequestParam String metodo) {
        List<Pedido> pedidos = pedidoRepository.findByMetodoPagoIgnoreCase(metodo);
        List<PedidoConsultaDTO> respuesta = pedidos.stream()
                .map(this::mapearADTO)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Método privado para convertir la entidad Pedido y sus detalles a DTO sin repetir código
    private PedidoConsultaDTO mapearADTO(Pedido ped) {
        List<DetallePedidoConsultaDTO> itemsDTO = (ped.getDetalles() != null)
                ? ped.getDetalles().stream().map(det -> new DetallePedidoConsultaDTO(
                        det.getProducto().getId(),
                        det.getProducto().getNombre(),
                        det.getCantidad(),
                        det.getPrecioUnitario()
                )).toList()
                : Collections.emptyList();

        return new PedidoConsultaDTO(
                ped.getId(),
                ped.getFecha(),
                ped.getMetodoPago(),
                ped.getCuotas(),
                ped.getTotal(),
                ped.getEstado(),
                itemsDTO
        );
    }
}
