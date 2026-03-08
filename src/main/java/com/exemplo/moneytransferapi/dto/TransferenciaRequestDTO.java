package com.exemplo.moneytransferapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        @NotNull Long idPagador,
        @NotNull Long idRecebedor,
        @NotNull @Positive BigDecimal valor) {

}
