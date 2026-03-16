package br.com.cwi.bancodigital.dto;

import br.com.cwi.bancodigital.domain.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID uuid, String name, BigDecimal balance) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getUuid(),
                account.getName(),
                account.getBalance()
        );
    }
}
