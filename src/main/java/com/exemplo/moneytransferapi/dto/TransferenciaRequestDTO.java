package com.exemplo.moneytransferapi.dto;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(Long idPagador, Long idRecebedor, BigDecimal valor) {

}
