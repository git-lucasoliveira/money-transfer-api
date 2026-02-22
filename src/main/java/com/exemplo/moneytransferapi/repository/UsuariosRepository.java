package com.exemplo.moneytransferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.moneytransferapi.domain.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {

}
