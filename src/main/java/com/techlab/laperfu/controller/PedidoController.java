package com.techlab.laperfu.controller;

import com.techlab.laperfu.dto.PedidoConsultaDTO;

import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.model.Pedido;
import com.techlab.laperfu.model.DetallePedido;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.PedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = "*") //Permite que el HTML se comunique con este puerto sin bloqueos de CORS
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository; // Lo usamos para buscar los productos reales por ID

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            // 1 Creamos la entidad Pedido principal
            Pedido pedido = new Pedido();
            pedido.setTotal(pedidoDTO.getTotal());
            // guardamos en la base de datos el método de pago y las cuotas
            pedido.setMetodoPago(pedidoDTO.getMetodoPago());
            pedido.setCuotas(pedidoDTO.getCuotas());

            List<DetallePedido> detalles = new ArrayList<>();

            // 2️ Recorremos cada ítem que mandó el JavaScript en el DTO
            for (PedidoDTO.ItemCarritoDTO itemDTO : pedidoDTO.getItems()) {
                // Buscamos si el producto existe en la base de datos
                Producto producto = productoRepository.findById(itemDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.getId()));

                // Creamos el renglón de detalle para este producto
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(itemDTO.getCantidad());
                detalle.setPrecioUnitario(itemDTO.getPrecio());

                detalles.add(detalle);
            }

            // Le asignamos la lista de detalles al pedido
            pedido.setDetalles(detalles);

            // Guardamos el pedido en MySQL (gracias al CascadeType.ALL se guardan los
            // detalles solitos)
            pedidoRepository.save(pedido);

            // Devolvemos la confirmación de éxito al front-end
            return ResponseEntity.ok(Map.of("status", "success", "mensaje", "¡Pedido guardado con éxito en MySQL!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "mensaje", e.getMessage()));
        }
    }

    // ENDPOINT PARA LISTAR PEDIDOS
    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodosLosPedidos() {
        try {
            List<Pedido> listaPedidos = pedidoRepository.findAll();
            return ResponseEntity.ok(listaPedidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

        // ENDPOINT PARA ACTUALIZAR EL ESTADO DEL PEDIDO (Maneja los 3 estados cíclicos)
    @CrossOrigin(origins = "*") 
    @PutMapping("/{id}/estado") // Modificamos la ruta para que coincida con el JS
    public ResponseEntity<?> cambiarEstadoPedido(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) { // Recibe el estado dinámico (?nuevoEstado=...)
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // Pasamos a mayúsculas para unificar el criterio en la base de datos
            String estadoUpper = nuevoEstado.toUpperCase();
            
            // Validamos que sea uno de los tres estados permitidos antes de guardar
            if (estadoUpper.equals("PENDIENTE") || estadoUpper.equals("EN_PROCESO") || estadoUpper.equals("REALIZADO")) {
                pedido.setEstado(estadoUpper);
            } else {
                throw new IllegalArgumentException("Estado no válido. Use: PENDIENTE, EN_PROCESO o REALIZADO");
            }

            pedidoRepository.save(pedido);

            // Devolvemos el formato exacto de éxito que espera tu JavaScript
            return ResponseEntity.ok(Map.of("status", "success", "mensaje", "Estado del pedido actualizado a: " + estadoUpper));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "mensaje", e.getMessage()));
        }
    }
    // No te olvides de importar el nuevo DTO arriba de todo en PedidoController.java:
// import com.techlab.laperfu.dto.PedidoConsultaDTO;

    // CONSULTA 1: Filtrar pedidos por estado
    // Ruta: GET http://localhost:8080/pedido/filtrar/estado?estado=PENDIENTE
    @GetMapping("/filtrar/estado")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorEstado(@RequestParam String estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstadoIgnoreCase(estado);
        
        // Transformamos cada entidad Pedido en nuestro DTO limpio
        List<PedidoConsultaDTO> respuesta = pedidos.stream().map(ped -> new PedidoConsultaDTO(
                ped.getId(),
                ped.getFecha(),
                ped.getMetodoPago(),
                ped.getCuotas(),
                ped.getTotal(),
                ped.getEstado(),
                ped.getDetalles() != null ? ped.getDetalles().size() : 0
        )).toList();

        return ResponseEntity.ok(respuesta);
    }

    // CONSULTA 2: Filtrar pedidos por método de pago
    // Ruta: GET http://localhost:8080/pedido/filtrar/pago?metodo=tarjeta
    @GetMapping("/filtrar/pago")
    public ResponseEntity<List<PedidoConsultaDTO>> filtrarPorMetodoPago(@RequestParam String metodo) {
        List<Pedido> pedidos = pedidoRepository.findByMetodoPagoIgnoreCase(metodo);
        
        List<PedidoConsultaDTO> respuesta = pedidos.stream().map(ped -> new PedidoConsultaDTO(
                ped.getId(),
                ped.getFecha(),
                ped.getMetodoPago(),
                ped.getCuotas(),
                ped.getTotal(),
                ped.getEstado(),
                ped.getDetalles() != null ? ped.getDetalles().size() : 0
        )).toList();

        return ResponseEntity.ok(respuesta);
    }



}
