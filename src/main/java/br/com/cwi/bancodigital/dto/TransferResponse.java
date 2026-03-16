package br.com.cwi.bancodigital.dto;

import br.com.cwi.bancodigital.domain.Transfer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID uuid,
        UUID fromAccountUuid,
        UUID toAccountUuid,
        BigDecimal amount,
        Instant createdAt
) {

    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(
                transfer.getUuid(),
                transfer.getFromAccount().getUuid(),
                transfer.getToAccount().getUuid(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }
}
