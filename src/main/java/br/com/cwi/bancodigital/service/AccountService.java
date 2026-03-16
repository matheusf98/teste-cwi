package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.dto.AccountRequest;
import br.com.cwi.bancodigital.dto.AccountResponse;
import br.com.cwi.bancodigital.exception.ResourceNotFoundException;
import br.com.cwi.bancodigital.repository.AccountRepository;
import br.com.cwi.bancodigital.specification.AccountSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return findAll(null, null, null);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll(String name, java.math.BigDecimal minBalance, java.math.BigDecimal maxBalance) {
        var spec = AccountSpecification.withFilters(name, minBalance, maxBalance);
        return accountRepository.findAll(spec, Sort.by("name")).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findByUuid(UUID uuid) {
        Account account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + uuid));
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse create(AccountRequest request) {
        Account account = new Account(request.name(), request.initialBalance());
        account = accountRepository.save(account);
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public Account getEntityByUuid(UUID uuid) {
        return accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + uuid));
    }
}
