package com.techlab.laperfu.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.techlab.laperfu.model.Usuario;
import com.techlab.laperfu.repository.PedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;
import com.techlab.laperfu.repository.UsuarioRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pedido crearPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getItems() == null || pedidoDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }

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

        Pedido pedido = new Pedido();
        pedido.setTotal(pedidoDTO.getTotal());
        pedido.setMetodoPago(pedidoDTO.getMetodoPago());
        pedido.setCuotas(pedidoDTO.getCuotas());

        // Asociamos el pedido al usuario autenticado mediante el token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            Usuario usuarioActual = usuarioRepository.findByEmail(auth.getName()).orElse(null);
            pedido.setUsuario(usuarioActual);
        }

        List<DetallePedido> detalles = new ArrayList<>();

        for (int i = 0; i < productosValidados.size(); i++) {
            Producto producto = productosValidados.get(i);
            PedidoDTO.ItemCarritoDTO itemDTO = pedidoDTO.getItems().get(i);

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

        if (estadoUpper.equals("CANCELADO") && !"CANCELADO".equals(estadoAnterior) && pedido.getDetalles() != null) {
            for (DetallePedido det : pedido.getDetalles()) {
                Producto prod = det.getProducto();
                prod.setStock(prod.getStock() + det.getCantidad());
                productoRepository.save(prod);
            }
        }

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Pedido> pedidos = esAdmin
                ? pedidoRepository.findAllByOrderByFechaDesc()
                : pedidoRepository.findByUsuarioEmailOrderByFechaDesc(auth != null ? auth.getName() : "");

        return pedidos.stream().map(this::mapearADTO).toList();
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

        String emailCliente = (ped.getUsuario() != null) ? ped.getUsuario().getEmail() : "Sin asignar";

        return new PedidoConsultaDTO(
                ped.getId(),
                ped.getFecha(),
                ped.getMetodoPago(),
                ped.getCuotas(),
                ped.getTotal(),
                ped.getEstado(),
                emailCliente,
                itemsDTO
        );
    }

    public PedidoConsultaDTO obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));
        return mapearADTO(pedido);
    }
}
