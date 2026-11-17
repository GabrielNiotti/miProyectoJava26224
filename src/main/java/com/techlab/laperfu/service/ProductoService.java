package com.techlab.laperfu.service;

import com.techlab.laperfu.exception.CategoriaNoEncontradaException;
import com.techlab.laperfu.exception.ProductoDuplicadoException;
import com.techlab.laperfu.exception.ProductoEnUsoException;
import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.exception.StockInsuficienteException;
import com.techlab.laperfu.model.Categoria;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.repository.CategoriaRepository;
import com.techlab.laperfu.repository.ProductoRepository;
import com.techlab.laperfu.util.Validador;
import com.techlab.laperfu.repository.DetallePedidoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

     @Autowired
    public ProductoService(ProductoRepository productoRepository, 
                           CategoriaRepository categoriaRepository,
                           DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id));
    }

       public Producto guardar(Producto producto) {
        // 1. Validamos todos los datos antes de guardar en MySQL
        Validador.validarNombre(producto.getNombre());

        // 2. Verificamos que no exista otro producto con el mismo nombre (HTTP 409)
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre().trim())) {
            throw new ProductoDuplicadoException("Ya existe un producto con el nombre: " + producto.getNombre());
        }

        Validador.validarPrecio(producto.getPrecio());
        Validador.validarStock(producto.getStock());
        Validador.validarCategoria(producto.getCategoria());

        // 3. Forzamos id = null para garantizar un INSERT nuevo y no pisar un producto existente
        producto.setId(null);

        // 4. Buscamos la categoría real en BD para devolverla completa en la respuesta JSON
        Categoria catReal = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + producto.getCategoria().getId()));
        producto.setCategoria(catReal);

        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datosNuevos) {
        // 1. Primero verificamos que el producto exista (si no, lanza 404 al instante)
        Producto productoExistente = obtenerPorId(id);

        // 2. Validamos los datos nuevos antes de actualizar
        Validador.validarNombre(datosNuevos.getNombre());
        if (productoRepository.existsByNombreIgnoreCaseAndIdNot(datosNuevos.getNombre().trim(), id)) {
            throw new ProductoDuplicadoException("Ya existe otro producto con el nombre: " + datosNuevos.getNombre());
        }
        Validador.validarPrecio(datosNuevos.getPrecio());
        Validador.validarStock(datosNuevos.getStock());
        Validador.validarCategoria(datosNuevos.getCategoria());

        // 3. Actualizamos todos los campos conservando descripcion e img de Laperfu
        productoExistente.setNombre(datosNuevos.getNombre());
        productoExistente.setPrecio(datosNuevos.getPrecio());
        productoExistente.setStock(datosNuevos.getStock());
        productoExistente.setDescripcion(datosNuevos.getDescripcion());
        
        if (datosNuevos.getImg() != null) {
            productoExistente.setImg(datosNuevos.getImg());
        }

        if (datosNuevos.getCategoria() != null) {
            Categoria catReal = categoriaRepository.findById(datosNuevos.getCategoria().getId())
                    .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + datosNuevos.getCategoria().getId()));
            productoExistente.setCategoria(catReal);
        }

        return productoRepository.save(productoExistente);
    }


       public boolean eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            return false; // El controlador devolverá 404 Not Found
        }

        // Verificamos si el producto ya está incluido en algún pedido antes de borrar
        if (detallePedidoRepository.existsByProductoId(id)) {
            throw new ProductoEnUsoException("No se puede eliminar el producto porque ya tiene pedidos asociados.");
        }

        productoRepository.deleteById(id);
        return true;
    }


    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

        public void descontarStock(Producto producto, int cantidadVendida) {
        if (producto.getStock() < cantidadVendida) {
            throw new StockInsuficienteException("Stock insuficiente para " + producto.getNombre()
                    + ". Disponible: " + producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidadVendida);
        productoRepository.save(producto);
    }

}
