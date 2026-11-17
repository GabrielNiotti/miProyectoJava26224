package com.techlab.laperfu.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.service.ProductoService;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);

        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto p) {
        Producto nuevo = productoService.guardar(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        return ResponseEntity.ok(productoService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productoService.eliminar(id);

        if (eliminado) {
            return ResponseEntity.noContent().build(); // Retorna HTTP 204 (Eliminado con éxito, sin cuerpo)
        } else {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 (No encontrado)
        }
    }

    // Dentro de tu clase ProductoController:

    // Ruta: GET http://localhost:8080/producto/buscar?nombre=versace
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    // Ruta: GET http://localhost:8080/producto/categoria/1
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoriaId));
    }

    // ENDPOINT PARA SUBIR IMÁGENES AL SERVIDOR
    // ENDPOINT PARA SUBIR IMÁGENES AL SERVIDOR
    @PostMapping("/subir-imagen")
    public ResponseEntity<?> subirImagen(
            @RequestParam("archivo") org.springframework.web.multipart.MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se seleccionó ningún archivo."));
            }

            // Limpiamos espacios o caracteres especiales del nombre del archivo
            String nombreLimpio = archivo.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String nombreArchivo = System.currentTimeMillis() + "_" + nombreLimpio;
            // 1. Guardamos en la carpeta fuente del proyecto (persistente)
            java.nio.file.Path carpetaSrc = java.nio.file.Paths.get(System.getProperty("user.dir"), "src", "main",
                    "resources", "static", "imagenes");
            if (!java.nio.file.Files.exists(carpetaSrc)) {
                java.nio.file.Files.createDirectories(carpetaSrc);
            }
            java.nio.file.Files.copy(archivo.getInputStream(), carpetaSrc.resolve(nombreArchivo),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 2. Copiamos también en target/classes para que se vea al instante sin
            // reiniciar
            java.nio.file.Path carpetaTarget = java.nio.file.Paths.get(System.getProperty("user.dir"), "target",
                    "classes", "static", "imagenes");
            if (java.nio.file.Files.exists(carpetaTarget)) {
                java.nio.file.Files.copy(carpetaSrc.resolve(nombreArchivo), carpetaTarget.resolve(nombreArchivo),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseEntity.ok(Map.of("nombreArchivo", nombreArchivo));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir imagen: " + e.getMessage()));
        }
    }

}
