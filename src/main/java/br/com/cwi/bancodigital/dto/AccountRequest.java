package br.com.cwi.bancodigital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotNull(message = "Saldo inicial é obrigatório")
        @DecimalMin(value = "0", message = "Saldo não pode ser negativo")
        BigDecimal initialBalance
) {
}
