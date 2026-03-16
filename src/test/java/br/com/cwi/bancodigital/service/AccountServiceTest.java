package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.dto.AccountRequest;
import br.com.cwi.bancodigital.exception.ResourceNotFoundException;
import br.com.cwi.bancodigital.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final UUID UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID UUID_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID UUID_999 = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void findAll_retornaListaDeContas() {
        Account account = new Account("Maria", new BigDecimal("100"));
        account.setId(1L);
        account.setUuid(UUID_1);
        when(accountRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(account));

        var result = accountService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).uuid()).isEqualTo(UUID_1);
        assertThat(result.get(0).name()).isEqualTo("Maria");
        assertThat(result.get(0).balance()).isEqualByComparingTo("100");
    }

    @Test
    void findByUuid_quandoExiste_retornaConta() {
        Account account = new Account("João", new BigDecimal("50"));
        account.setId(2L);
        account.setUuid(UUID_2);
        when(accountRepository.findByUuid(UUID_2)).thenReturn(Optional.of(account));

        var result = accountService.findByUuid(UUID_2);

        assertThat(result.uuid()).isEqualTo(UUID_2);
        assertThat(result.name()).isEqualTo("João");
        assertThat(result.balance()).isEqualByComparingTo("50");
    }

    @Test
    void findByUuid_quandoNaoExiste_lancaResourceNotFoundException() {
        when(accountRepository.findByUuid(UUID_999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findByUuid(UUID_999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(UUID_999.toString());
    }

    @Test
    void create_salvaERetornaConta() {
        AccountRequest request = new AccountRequest("Ana", new BigDecimal("200"));
        Account saved = new Account("Ana", new BigDecimal("200"));
        saved.setId(3L);
        saved.setUuid(UUID_3);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        var result = accountService.create(request);

        assertThat(result.uuid()).isEqualTo(UUID_3);
        assertThat(result.name()).isEqualTo("Ana");
        assertThat(result.balance()).isEqualByComparingTo("200");
        verify(accountRepository).save(any(Account.class));
    }
}
