package com.techlab.laperfu.controller;

import com.techlab.laperfu.dto.PedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/pedido")
@Tag(name = "Pedidos", description = "Gestión de compras, estados y consultas de pedidos de La Perfu")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Registra una compra de perfumes y descuenta automáticamente el stock")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        pedidoService.crearPedido(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", "success", "mensaje", "¡Pedido guardado con éxito y stock actualizado!"));
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Devuelve el historial completo de pedidos realizados")
    public ResponseEntity<List<PedidoConsultaDTO>> listarTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Modifica el estado de un pedido existente (ej. PENDIENTE, PAGADO, ENVIADO)")
    public ResponseEntity<?> cambiarEstadoPedido(
            @Parameter(description = "ID del pedido a actualizar", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido", example = "ENVIADO") @RequestParam String nuevoEstado) {
        String estadoActualizado = pedidoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(Map.of("status", "success", "mensaje", "Estado del pedido actualizado a: " + estadoActualizado));
    }

    @GetMapping("/filtrar/estado")
    @Operation(summary = "Filtrar pedidos por estado", description = "Devuelve los pedidos que coinciden con un estado específico")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorEstado(
            @Parameter(description = "Estado por el cual filtrar", example = "PENDIENTE") @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.filtrarPorEstado(estado));
    }

    @GetMapping("/filtrar/pago")
    @Operation(summary = "Filtrar pedidos por método de pago", description = "Devuelve los pedidos realizados con un medio de pago determinado")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorMetodoPago(
            @Parameter(description = "Método de pago (ej. TRANSFERENCIA, TARJETA, EFECTIVO)", example = "TRANSFERENCIA") @RequestParam String metodo) {
        return ResponseEntity.ok(pedidoService.filtrarPorMetodoPago(metodo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Busca y devuelve el detalle completo de un pedido específico")
    public ResponseEntity<PedidoConsultaDTO> obtenerPedidoPorId(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

}
