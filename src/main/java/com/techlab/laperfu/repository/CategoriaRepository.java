package com.techlab.laperfu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techlab.laperfu.model.Categoria;

@Repository 
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
