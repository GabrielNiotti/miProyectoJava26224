package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.ProductoDuplicadoException;
import com.techlab.laperfu.exception.ProductoEnUsoException;
import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.exception.StockInsuficienteException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.CategoriaRepository;
import com.techlab.laperfu.repository.DetallePedidoRepository;
import com.techlab.laperfu.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoriaFemenina;
    private Producto perfumeEjemplo;

    @BeforeEach //metodo 
    void setUp() {
        // Se ejecuta antes de cada @Test para tener datos limpios
        categoriaFemenina = new Categoria(1L, "Fragancias Femeninas");
        perfumeEjemplo = new Producto(
                1L,
                "La Vie Est Belle EDP 100ml",
                new BigDecimal("145000.00"),
                10,
                "Notas de iris y jazmín",
                categoriaFemenina
        );
    }

    @Test
    void obtenerPorId_CuandoExiste_RetornaProducto() {
        // Arrange (Preparar)
        when(productoRepository.findById(1L)).thenReturn(Optional.of(perfumeEjemplo));

        // Act (Actuar)
        Producto resultado = productoService.obtenerPorId(1L);

        // Assert (Verificar)
        assertNotNull(resultado);
        assertEquals("La Vie Est Belle EDP 100ml", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_LanzaExcepcion() {
        // Arrange
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductoNoEncontradoException.class, () -> {
            productoService.obtenerPorId(999L);
        });
    }

    @Test
    void guardar_CuandoDatosSonValidos_GuardaYRetornaProducto() {
        // Arrange
        when(productoRepository.existsByNombreIgnoreCase("La Vie Est Belle EDP 100ml")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaFemenina));
        when(productoRepository.save(any(Producto.class))).thenReturn(perfumeEjemplo);

        // Act
        Producto guardado = productoService.guardar(perfumeEjemplo);

        // Assert
        assertNotNull(guardado);
        assertEquals("La Vie Est Belle EDP 100ml", guardado.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void guardar_CuandoNombreYaExiste_LanzaProductoDuplicadoException() {
        // Arrange
        when(productoRepository.existsByNombreIgnoreCase("La Vie Est Belle EDP 100ml")).thenReturn(true);

        // Act & Assert
        assertThrows(ProductoDuplicadoException.class, () -> {
            productoService.guardar(perfumeEjemplo);
        });
        // Verificamos que NUNCA intentó guardar en la base
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_CuandoTienePedidosAsociados_LanzaProductoEnUsoException() {
        // Arrange
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(detallePedidoRepository.existsByProductoId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ProductoEnUsoException.class, () -> {
            productoService.eliminar(1L);
        });
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void descontarStock_CuandoAlcanza_RestaCorrectamente() {
        // Arrange (perfumeEjemplo arranca con stock = 10)
        when(productoRepository.save(any(Producto.class))).thenReturn(perfumeEjemplo);

        // Act
        productoService.descontarStock(perfumeEjemplo, 3);

        // Assert
        assertEquals(7, perfumeEjemplo.getStock());
        verify(productoRepository).save(perfumeEjemplo);
    }

    @Test
    void descontarStock_CuandoNoAlcanza_LanzaStockInsuficienteException() {
        // Act & Assert (tiene 10 en stock, pedimos 15)
        assertThrows(StockInsuficienteException.class, () -> {
            productoService.descontarStock(perfumeEjemplo, 15);
        });
        verify(productoRepository, never()).save(any());
    }
}
