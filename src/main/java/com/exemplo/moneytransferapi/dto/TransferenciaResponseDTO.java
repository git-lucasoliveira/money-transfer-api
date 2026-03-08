package com.exemplo.moneytransferapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaResponseDTO(
                Long id,
                Long idPagador,
                Long idRecebedor,
                BigDecimal valor,
                LocalDateTime dataTransferencia) {
}
