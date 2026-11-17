package com.techlab.laperfu.repository;

import com.techlab.laperfu.model.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 1. Buscar productos con palabra clave, ignorando mayusculas y minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /// 2. Filtrar todos los productos que pertenezcan a una categoría específica
    List<Producto> findByCategoriaId(Long categoriaId);

    // 3. Buscar productos con poco stock (ej: menor o igual a 5 unidades)
    List<Producto> findByStockLessThanEqual(Integer cantidad);
    
}