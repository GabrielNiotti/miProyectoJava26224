package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    public Producto obtenerPorId(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id));
}


    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        producto.setId(id); // ajusta al nombre real del setter del id
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}