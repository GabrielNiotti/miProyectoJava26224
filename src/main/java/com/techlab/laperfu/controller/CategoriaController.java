package com.techlab.laperfu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/categoria")
@Tag(name = "Categorías", description = "Consulta y administración de categorías de perfumes de La Perfu")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Devuelve el listado completo de categorías de perfumes disponibles")
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Busca y devuelve una categoría específica según su identificador único")
    public ResponseEntity<Categoria> obtener(
            @Parameter(description = "ID de la categoría", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría (ADMIN)", description = "Agrega una nueva categoría al catálogo. Requiere rol ADMIN.")
    public ResponseEntity<Categoria> crear(@RequestBody Categoria c) {
        Categoria nueva = categoriaService.guardar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría (ADMIN)", description = "Modifica el nombre de una categoría existente por su ID. Requiere rol ADMIN.")
    public ResponseEntity<Categoria> actualizar(
            @Parameter(description = "ID de la categoría a actualizar", example = "1") @PathVariable Long id,
            @RequestBody Categoria datos) {
        return ResponseEntity.ok(categoriaService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría (ADMIN)", description = "Elimina una categoría por su ID. Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoría a eliminar", example = "1") @PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
