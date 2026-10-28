package com.techlab.laperfu.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.techlab.laperfu.exception.ProductoNoEncontradoException;
import com.techlab.laperfu.model.Crema;
import com.techlab.laperfu.model.Perfume;
import com.techlab.laperfu.model.Producto;
import com.techlab.laperfu.model.Shampoo;
import com.techlab.laperfu.util.Validador;

@Service 
public class ProductoService {
    
    // Colección en memoria que guarda los productos
    private final List<Producto> productos = new ArrayList<>();

    // CREATE: agregar un nuevo producto
    public Producto guardar(Producto p) {
        Validador.validarNombre(p.getNombre());
        Validador.validarPrecio(p.getPrecio());
        Validador.validarStock(p.getStock());
        Validador.validarCategoria(p.getCategoria());
        p.setId((++Producto.contadorId));

        productos.add(p);
        return p;
    }

    // READ: devuelve toda la lista de productos
    public List<Producto> listarTodos(){
        return productos;
    }

    // READ: buscar por id utilizando Streams de Java 8+
    public Producto obtenerPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró un producto con el id " + id));
    }

    // UPDATE: actualiza los datos comunes y específicos de la subclase
    public Producto actualizar(Long id, Producto datos){
        // Si lanza excepción, la actualización se cancela automáticamente
        Producto p = obtenerPorId(id);

        // Validamos los nuevos datos antes de aplicarlos
        Validador.validarNombre(datos.getNombre());
        Validador.validarPrecio(datos.getPrecio());
        Validador.validarStock(datos.getStock());
        Validador.validarCategoria(datos.getCategoria());

        // Modificamos los campos comunes generados por Lombok
        p.setNombre(datos.getNombre());
        p.setPrecio(datos.getPrecio());
        p.setStock(datos.getStock());

        // Java 14/17: Pattern Matching para instanceof
        if (p instanceof Crema c && datos instanceof Crema dc) {
            c.setTipoPiel(dc.getTipoPiel());
        } else if (p instanceof Perfume perf && datos instanceof Perfume dperf) {
            perf.setGenero(dperf.getGenero());
        } else if (p instanceof Shampoo s && datos instanceof Shampoo ds) {
            s.setTipoCabello(ds.getTipoCabello());
        }

        return p;
    }

    // DELETE: eliminar un producto por ID
    public void eliminar(Long id){
        Producto p = obtenerPorId(id);
        productos.remove(p);
    }

    // Métodos secundarios de búsqueda y verificación
    public Producto buscarPorId(Long id) {
        return obtenerPorId(id);
    }

    public boolean existeProducto(Long id) {
        return productos.stream().anyMatch(p -> p.getId().equals(id));
    }
}
