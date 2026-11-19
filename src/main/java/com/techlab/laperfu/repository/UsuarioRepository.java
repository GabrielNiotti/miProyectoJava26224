package com.techlab.laperfu.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techlab.laperfu.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
