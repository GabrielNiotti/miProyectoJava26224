package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.ProductoNoEncontradoException;

import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        producto.setId(id); // ajusta al nombre real del setter del id
        return productoRepository.save(producto);
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Dentro de tu clase ProductoService:

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

}