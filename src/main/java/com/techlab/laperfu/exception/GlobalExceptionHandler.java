package com.techlab.laperfu.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // atiende los errores de TODOS los controladores
public class GlobalExceptionHandler {

    // Si algún método lanza ProductoNoEncontradoException, se ejecuta este
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<String> manejarNoEncontrado(ProductoNoEncontradoException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<String> manejarStock(StockInsuficienteException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
    }

    // Manejador para cuando no se encuentra una categoría (404)
    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<String> manejarCategoriaNoEncontrada(CategoriaNoEncontradaException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
    }

    // Manejador para las reglas de tu clase Validador (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
    }

        @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarProductoDuplicado(ProductoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // HTTP 409 Conflict
                .body(Map.of("error", ex.getMessage()));
    }
        @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarPedidoNoEncontrado(PedidoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 Not Found
                .body(Map.of("error", e.getMessage()));
    }

        @ExceptionHandler(ProductoEnUsoException.class)
    public ResponseEntity<Map<String, String>> manejarProductoEnUso(ProductoEnUsoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                .body(Map.of("error", e.getMessage()));
    }




}
