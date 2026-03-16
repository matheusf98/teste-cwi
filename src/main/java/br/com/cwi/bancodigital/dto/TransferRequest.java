package br.com.cwi.bancodigital.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "UUID da conta de origem é obrigatório")
        UUID fromAccountUuid,

        @NotNull(message = "UUID da conta de destino é obrigatório")
        UUID toAccountUuid,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal amount
) {
}
