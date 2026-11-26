package com.techlab.laperfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techlab.laperfu.dto.AuthRequest;
import com.techlab.laperfu.model.Usuario;
import com.techlab.laperfu.repository.UsuarioRepository;
import com.techlab.laperfu.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setEmail("cliente@laperfu.com");
        usuarioEjemplo.setPassword("$2a$10$hashEncriptadoSimulado");
        usuarioEjemplo.setRol("USER");
    }

    @Test
    void registrar_CuandoDatosSonValidos_Retorna201YRolUser() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("nuevo@laperfu.com");
        request.setPassword("1234");

        when(usuarioRepository.findByEmail("nuevo@laperfu.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$hashEncriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nuevo@laperfu.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    void registrar_CuandoEmailYaExiste_Retorna409Conflict() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("cliente@laperfu.com");
        request.setPassword("1234");

        when(usuarioRepository.findByEmail("cliente@laperfu.com")).thenReturn(Optional.of(usuarioEjemplo));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un usuario registrado con ese email."));
    }

    @Test
    void registrar_CuandoFaltaPassword_Retorna400BadRequest() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("incompleto@laperfu.com");
        request.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_CuandoCredencialesSonCorrectas_Retorna200YTokenJwt() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("cliente@laperfu.com");
        request.setPassword("1234");

        when(usuarioRepository.findByEmail("cliente@laperfu.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("1234", "$2a$10$hashEncriptadoSimulado")).thenReturn(true);
        when(jwtService.generarToken(usuarioEjemplo)).thenReturn("eyJhbGciOiJIUzI1NiJ9.tokenDePrueba");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.tokenDePrueba"))
                .andExpect(jsonPath("$.email").value("cliente@laperfu.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    void login_CuandoPasswordEsIncorrecta_Retorna401Unauthorized() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("cliente@laperfu.com");
        request.setPassword("claveEquivocada");

        when(usuarioRepository.findByEmail("cliente@laperfu.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("claveEquivocada", "$2a$10$hashEncriptadoSimulado")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas."));
    }
}
