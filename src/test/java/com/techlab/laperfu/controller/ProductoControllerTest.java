package com.techlab.laperfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.service.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    private Producto perfumeEjemplo;

    @BeforeEach
    void setUp() {
        Categoria cat = new Categoria(1L, "Fragancias Femeninas");
        perfumeEjemplo = new Producto(
                1L,
                "Good Girl Carolina Herrera 80ml",
                new BigDecimal("195000.00"),
                12,
                "Notas de almendra, café, jazmín y cacao",
                cat
        );
    }

    @Test
    void listarProductos_EsPublico_Retorna200YListaJson() throws Exception {
        // Arrange
        when(productoService.listarTodos()).thenReturn(List.of(perfumeEjemplo));

        // Act & Assert (sin usuario autenticado)
        mockMvc.perform(get("/producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Good Girl Carolina Herrera 80ml"))
                .andExpect(jsonPath("$[0].stock").value(12));
    }

    @Test
    void obtenerProductoPorId_CuandoNoExiste_Retorna404() throws Exception {
        // Arrange
        when(productoService.obtenerPorId(999L))
                .thenThrow(new ProductoNoEncontradoException("Producto no encontrado con el ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/producto/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearProducto_SinAutenticacion_Retorna403() throws Exception {
        // Act & Assert (sin token ni usuario)
        mockMvc.perform(post("/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfumeEjemplo)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void crearProducto_ConRolUser_Retorna403() throws Exception {
        // Act & Assert (un cliente USER no puede crear perfumes)
        mockMvc.perform(post("/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfumeEjemplo)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearProducto_ConRolAdmin_Retorna201YProductoCreado() throws Exception {
        // Arrange
        when(productoService.guardar(any(Producto.class))).thenReturn(perfumeEjemplo);

        // Act & Assert (un ADMIN sí puede crear perfumes)
        mockMvc.perform(post("/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(perfumeEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Good Girl Carolina Herrera 80ml"));
    }
}
