package com.exemplo.moneytransferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.moneytransferapi.domain.Usuario;

import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
