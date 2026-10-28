package com.techlab.laperfu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET: Listar todos
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // GET: Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerPorId(id));
        } catch (ProductoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST: Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto nuevoProducto) {
        // Al usar el constructor vacío de Jackson, se necesita guardar para que se agregue a la lista
        Producto guardado = productoService.guardar(nuevoProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT: Actualizar producto existente (SOLUCIÓN DEFINITIVA AL ID QUE NO EXISTE)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoEditado) {

        // 1. Validamos con el método corregido del Service si el producto realmente existe
        if (!productoService.existeProducto(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: El producto con ID " + id + " no existe en el sistema.");
        }

        // 2. Si existe, llamamos al método actualizar de tu Service que modifica la lista en memoria
        Producto resultado = productoService.actualizar(id, productoEditado);
        return ResponseEntity.ok(resultado);
    }

    // DELETE: Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
