package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.ProductoRepository;
import com.techlab.laperfu.util.Validador; // 1. Importamos tu Validador
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
        // 2. Validamos todos los datos antes de guardar en MySQL
        Validador.validarNombre(producto.getNombre());
        Validador.validarPrecio(producto.getPrecio());
        Validador.validarStock(producto.getStock());
        Validador.validarCategoria(producto.getCategoria());

        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datosNuevos) {
        // 3. Validamos los datos nuevos antes de actualizar
        Validador.validarNombre(datosNuevos.getNombre());
        Validador.validarPrecio(datosNuevos.getPrecio());
        Validador.validarStock(datosNuevos.getStock());
        Validador.validarCategoria(datosNuevos.getCategoria());

        // 4. Verificamos que el producto exista (si no, lanza tu error 404 automáticamente)
        Producto productoExistente = obtenerPorId(id);

        // 5. Actualizamos todos los campos usando los setters
        productoExistente.setNombre(datosNuevos.getNombre());
        productoExistente.setPrecio(datosNuevos.getPrecio());
        productoExistente.setStock(datosNuevos.getStock());
        productoExistente.setDescripcion(datosNuevos.getDescripcion());
        
        if (datosNuevos.getImg() != null) {
            productoExistente.setImg(datosNuevos.getImg());
        }

        if (datosNuevos.getCategoria() != null) {
            productoExistente.setCategoria(datosNuevos.getCategoria());
        }

        return productoRepository.save(productoExistente);
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
}
