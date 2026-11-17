package com.techlab.laperfu.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.laperfu.dto.DetallePedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoConsultaDTO;
import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.exception.PedidoNoEncontradoException;
import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.exception.StockInsuficienteException;
import com.techlab.laperfu.model.DetallePedido;
import com.techlab.laperfu.model.Pedido;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.PedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    // POST /pedido (Lógica profesional en 2 pasos: Validar todo -> Guardar y descontar stock)
    @Transactional
    public Pedido crearPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getItems() == null || pedidoDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }

        // --- PASO 1: Validar TODAS las líneas sin tocar la base de datos ---
        List<Producto> productosValidados = new ArrayList<>();
        List<Long> idsVistos = new ArrayList<>();

        for (PedidoDTO.ItemCarritoDTO itemDTO : pedidoDTO.getItems()) {
            if (itemDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            if (idsVistos.contains(itemDTO.getId())) {
                throw new IllegalArgumentException("El producto con ID " + itemDTO.getId() + " está repetido en el carrito.");
            }
            idsVistos.add(itemDTO.getId());

            Producto producto = productoRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + itemDTO.getId()));

            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ", solicitado: " + itemDTO.getCantidad());
            }

            productosValidados.add(producto);
        }

        // --- PASO 2: Todo es válido -> Guardamos pedido, detalles y descontamos stock ---
        Pedido pedido = new Pedido();
        pedido.setTotal(pedidoDTO.getTotal());
        pedido.setMetodoPago(pedidoDTO.getMetodoPago());
        pedido.setCuotas(pedidoDTO.getCuotas());

        List<DetallePedido> detalles = new ArrayList<>();

        for (int i = 0; i < productosValidados.size(); i++) {
            Producto producto = productosValidados.get(i);
            PedidoDTO.ItemCarritoDTO itemDTO = pedidoDTO.getItems().get(i);

            // Descontamos stock real en MySQL
            producto.setStock(producto.getStock() - itemDTO.getCantidad());
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setPrecioUnitario(itemDTO.getPrecio());
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        return pedidoRepository.save(pedido);
    }

    // PUT /pedido/{id}/estado (Actualiza estado y gestiona devolución/descuento de stock si se cancela o reactiva)
        @Transactional
    public String actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));


        String estadoAnterior = pedido.getEstado();
        String estadoUpper = nuevoEstado.toUpperCase();

        if (!estadoUpper.equals("PENDIENTE") && !estadoUpper.equals("EN_PROCESO")
                && !estadoUpper.equals("REALIZADO") && !estadoUpper.equals("CANCELADO")) {
            throw new IllegalArgumentException("Estado no válido. Use: PENDIENTE, EN_PROCESO, REALIZADO o CANCELADO.");
        }

        // Si el pedido se CANCELA (y antes no estaba cancelado), devolvemos el stock a cada producto
        if (estadoUpper.equals("CANCELADO") && !"CANCELADO".equals(estadoAnterior) && pedido.getDetalles() != null) {
            for (DetallePedido det : pedido.getDetalles()) {
                Producto prod = det.getProducto();
                prod.setStock(prod.getStock() + det.getCantidad());
                productoRepository.save(prod);
            }
        }

        // Si un pedido CANCELADO se vuelve a REACTIVAR (ej. a PENDIENTE), volvemos a descontar el stock
        if (!estadoUpper.equals("CANCELADO") && "CANCELADO".equals(estadoAnterior) && pedido.getDetalles() != null) {
            for (DetallePedido det : pedido.getDetalles()) {
                Producto prod = det.getProducto();
                if (prod.getStock() < det.getCantidad()) {
                    throw new StockInsuficienteException("No hay stock suficiente para reactivar el pedido del producto: " + prod.getNombre());
                }
                prod.setStock(prod.getStock() - det.getCantidad());
                productoRepository.save(prod);
            }
        }

        pedido.setEstado(estadoUpper);
        pedidoRepository.save(pedido);
        return estadoUpper;
    }

    public List<PedidoConsultaDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::mapearADTO).toList();
    }

    public List<PedidoConsultaDTO> filtrarPorEstado(String estado) {
        return pedidoRepository.findByEstadoIgnoreCase(estado).stream().map(this::mapearADTO).toList();
    }

    public List<PedidoConsultaDTO> filtrarPorMetodoPago(String metodo) {
        return pedidoRepository.findByMetodoPagoIgnoreCase(metodo).stream().map(this::mapearADTO).toList();
    }

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

        public PedidoConsultaDTO obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));
        return mapearADTO(pedido);
    }

}
