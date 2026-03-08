package com.exemplo.moneytransferapi.dto;

import com.exemplo.moneytransferapi.domain.TipoUsuario;

import java.math.BigDecimal;

public record UsuarioResponseDTO(
        Long id,
        String nomeCompleto,
        String email,
        String cpf,
        TipoUsuario tipo,
        BigDecimal saldo
) {}

