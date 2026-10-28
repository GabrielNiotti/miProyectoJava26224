package com.techlab.laperfu.exception;

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

    @ExceptionHandler (StockInsuficienteException.class)
    public ResponseEntity<String> manejarStock(StockInsuficienteException e) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());   // 400
    }
    // Nombre, precio o categoria invalidos
}