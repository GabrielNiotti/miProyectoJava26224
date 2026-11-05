package com.techlab.laperfu.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.techlab.laperfu.exception.CategoriaNoEncontradaException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.repository.CategoriaRepository;

@Service 
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    } 
    
     public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con id: " + id));
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        return categoriaRepository.save(existente);
    }

    public void eliminar(Long id) {
        Categoria existente = obtenerPorId(id);
        categoriaRepository.delete(existente);
    }  

}
