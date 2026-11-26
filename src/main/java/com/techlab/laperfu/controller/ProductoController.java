package com.techlab.laperfu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // <-- AGREGAR ESTE IMPORT
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "bearerAuth") // <-- AGREGAR ESTA ANOTACIÓN
@RestController
@RequestMapping("/producto")
@Tag(name = "Productos", description = "Catálogo y administración de perfumes de La Perfu")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Devuelve el listado completo de perfumes disponibles en la base de datos")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca y devuelve un perfume específico según su identificador único")
    public ResponseEntity<Producto> obtener(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto (ADMIN)", description = "Guarda un nuevo perfume en el catálogo. Requiere rol ADMIN.")
    public ResponseEntity<Producto> crear(@RequestBody Producto p) {
        Producto nuevo = productoService.guardar(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto (ADMIN)", description = "Modifica los datos de un perfume existente por su ID. Requiere rol ADMIN.")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1") @PathVariable Long id,
            @RequestBody Producto datos) {
        return ResponseEntity.ok(productoService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto (ADMIN)", description = "Elimina un perfume del catálogo por su ID. Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", example = "1") @PathVariable Long id) {
        boolean eliminado = productoService.eliminar(id);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Filtra perfumes cuyo nombre coincida con el texto ingresado")
    public ResponseEntity<List<Producto>> buscarPorNombre(
            @Parameter(description = "Nombre o marca a buscar", example = "versace") @RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar productos por categoría", description = "Devuelve todos los perfumes pertenecientes a una categoría específica")
    public ResponseEntity<List<Producto>> listarPorCategoria(
            @Parameter(description = "ID de la categoría", example = "1") @PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoriaId));
    }

    @PostMapping(value = "/subir-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen de producto (ADMIN)", description = "Guarda una imagen en el servidor y devuelve el nombre de archivo generado")
    public ResponseEntity<?> subirImagen(
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se seleccionó ningún archivo."));
            }

            String nombreLimpio = archivo.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String nombreArchivo = System.currentTimeMillis() + "_" + nombreLimpio;
            
            java.nio.file.Path carpetaSrc = java.nio.file.Paths.get(System.getProperty("user.dir"), "src", "main",
                    "resources", "static", "imagenes");
            if (!java.nio.file.Files.exists(carpetaSrc)) {
                java.nio.file.Files.createDirectories(carpetaSrc);
            }
            java.nio.file.Files.copy(archivo.getInputStream(), carpetaSrc.resolve(nombreArchivo),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

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
