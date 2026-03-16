package br.com.cwi.bancodigital.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovementResponse(
        UUID transferUuid,
        String type,
        UUID relatedAccountUuid,
        String relatedAccountName,
        BigDecimal amount,
        Instant createdAt
) {
}
