package com.techlab.laperfu.service;

import com.techlab.laperfu.dto.PedidoDTO;
import com.techlab.laperfu.exception.PedidoNoEncontradoException;
import com.techlab.laperfu.exception.StockInsuficienteException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.model.DetallePedido;
import com.techlab.laperfu.model.Pedido;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.PedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;
import com.techlab.laperfu.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Producto perfumeEjemplo;

    @BeforeEach
    void setUp() {
        Categoria cat = new Categoria(1L, "Fragancias Masculinas");
        perfumeEjemplo = new Producto(
                1L,
                "Sauvage Dior 100ml",
                new BigDecimal("180000.00"),
                10,
                "Notas frescas y amaderadas",
                cat
        );
    }

    @Test
    void crearPedido_CuandoHayStock_CreaPedidoYDescuentaStock() {
        // Arrange
        PedidoDTO.ItemCarritoDTO item = new PedidoDTO.ItemCarritoDTO();
        item.setId(1L);
        item.setCantidad(2);
        item.setPrecio(new BigDecimal("180000.00"));

        PedidoDTO dto = new PedidoDTO();
        dto.setTotal(new BigDecimal("360000.00"));
        dto.setMetodoPago("TARJETA");
        dto.setCuotas(3);
        dto.setItems(List.of(item));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(perfumeEjemplo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Pedido resultado = pedidoService.crearPedido(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(8, perfumeEjemplo.getStock()); // 10 - 2 = 8
        assertEquals(1, resultado.getDetalles().size());
        verify(productoRepository).save(perfumeEjemplo);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_CuandoCarritoEstaVacio_LanzaIllegalArgumentException() {
        // Arrange
        PedidoDTO dto = new PedidoDTO();
        dto.setItems(Collections.emptyList());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.crearPedido(dto);
        });
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crearPedido_CuandoStockEsInsuficiente_LanzaStockInsuficienteException() {
        // Arrange (perfumeEjemplo tiene stock = 10, pedimos 15)
        PedidoDTO.ItemCarritoDTO item = new PedidoDTO.ItemCarritoDTO();
        item.setId(1L);
        item.setCantidad(15);
        item.setPrecio(new BigDecimal("180000.00"));

        PedidoDTO dto = new PedidoDTO();
        dto.setItems(List.of(item));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(perfumeEjemplo));

        // Act & Assert
        assertThrows(StockInsuficienteException.class, () -> {
            pedidoService.crearPedido(dto);
        });
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_CuandoSeCancelaPedido_DevuelveStockAlProducto() {
        // Arrange (stock actual en 8, pedido tenía 2 unidades)
        perfumeEjemplo.setStock(8);

        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(perfumeEjemplo);
        detalle.setCantidad(2);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");
        pedido.setDetalles(List.of(detalle));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        String nuevoEstado = pedidoService.actualizarEstado(1L, "CANCELADO");

        // Assert
        assertEquals("CANCELADO", nuevoEstado);
        assertEquals(10, perfumeEjemplo.getStock()); // 8 + 2 = 10 devueltos
        verify(productoRepository).save(perfumeEjemplo);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void actualizarEstado_CuandoEstadoEsInvalido_LanzaIllegalArgumentException() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.actualizarEstado(1L, "ESTADO_INVENTADO");
        });
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_CuandoNoExiste_LanzaPedidoNoEncontradoException() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNoEncontradoException.class, () -> {
            pedidoService.obtenerPorId(999L);
        });
    }
}
