package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.CategoriaNoEncontradaException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.repository.CategoriaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaEjemplo;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria(1L, "Fragancias Femeninas");
    }

    @Test
    void listarTodos_RetornaListaDeCategorias() {
        // Arrange
        Categoria cat2 = new Categoria(2L, "Fragancias Masculinas");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaEjemplo, cat2));

        // Act
        List<Categoria> resultado = categoriaService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_RetornaCategoria() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));

        // Act
        Categoria resultado = categoriaService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Fragancias Femeninas", resultado.getNombre());
    }

    @Test
    void obtenerPorId_CuandoNoExiste_LanzaExcepcion() {
        // Arrange
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoriaNoEncontradaException.class, () -> {
            categoriaService.obtenerPorId(999L);
        });
    }

    @Test
    void guardar_RetornaCategoriaGuardada() {
        // Arrange
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaEjemplo);

        // Act
        Categoria guardada = categoriaService.guardar(categoriaEjemplo);

        // Assert
        assertNotNull(guardada);
        assertEquals("Fragancias Femeninas", guardada.getNombre());
        verify(categoriaRepository).save(categoriaEjemplo);
    }

    @Test
    void actualizar_CuandoExiste_ActualizaNombreYGuarda() {
        // Arrange
        Categoria datosNuevos = new Categoria(null, "Fragancias Unisex");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Categoria actualizada = categoriaService.actualizar(1L, datosNuevos);

        // Assert
        assertEquals("Fragancias Unisex", actualizada.getNombre());
        verify(categoriaRepository).save(categoriaEjemplo);
    }

    @Test
    void eliminar_CuandoExiste_EliminaCategoria() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));

        // Act
        categoriaService.eliminar(1L);

        // Assert
        verify(categoriaRepository).delete(categoriaEjemplo);
    }

    @Test
    void eliminar_CuandoNoExiste_LanzaExcepcionYNoElimina() {
        // Arrange
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoriaNoEncontradaException.class, () -> {
            categoriaService.eliminar(999L);
        });
        verify(categoriaRepository, never()).delete(any());
    }
}
