package com.techlab.laperfu.controller;

import com.techlab.laperfu.dto.PedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        pedidoService.crearPedido(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", "success", "mensaje", "¡Pedido guardado con éxito y stock actualizado!"));
    }

    @GetMapping
    public ResponseEntity<List<PedidoConsultaDTO>> listarTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestParam String nuevoEstado) {
        String estadoActualizado = pedidoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(Map.of("status", "success", "mensaje", "Estado del pedido actualizado a: " + estadoActualizado));
    }

    @GetMapping("/filtrar/estado")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.filtrarPorEstado(estado));
    }

    @GetMapping("/filtrar/pago")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorMetodoPago(@RequestParam String metodo) {
        return ResponseEntity.ok(pedidoService.filtrarPorMetodoPago(metodo));
    }
        @GetMapping("/{id}")
    public ResponseEntity<PedidoConsultaDTO> obtenerPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

}
