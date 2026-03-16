package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.domain.Transfer;
import br.com.cwi.bancodigital.dto.TransferRequest;
import br.com.cwi.bancodigital.exception.BusinessException;
import br.com.cwi.bancodigital.exception.ResourceNotFoundException;
import br.com.cwi.bancodigital.repository.AccountRepository;
import br.com.cwi.bancodigital.repository.TransferRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final UUID UUID_FROM = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_TO = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID UUID_TRANSFER = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID UUID_ACCOUNT = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_999 = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfer_quandoContasValidas_executaTransferencia() {
        Account from = new Account("Maria", new BigDecimal("1000"));
        from.setId(1L);
        from.setUuid(UUID_FROM);
        Account to = new Account("João", new BigDecimal("500"));
        to.setId(2L);
        to.setUuid(UUID_TO);

        when(accountRepository.findByUuid(UUID_FROM)).thenReturn(Optional.of(from));
        when(accountRepository.findByUuid(UUID_TO)).thenReturn(Optional.of(to));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        Transfer saved = new Transfer(from, to, new BigDecimal("100"));
        saved.setId(10L);
        saved.setUuid(UUID_TRANSFER);
        when(transferRepository.save(any(Transfer.class))).thenReturn(saved);

        TransferRequest request = new TransferRequest(UUID_FROM, UUID_TO, new BigDecimal("100"));
        var result = transferService.transfer(request);

        assertThat(result.uuid()).isEqualTo(UUID_TRANSFER);
        assertThat(result.fromAccountUuid()).isEqualTo(UUID_FROM);
        assertThat(result.toAccountUuid()).isEqualTo(UUID_TO);
        assertThat(result.amount()).isEqualByComparingTo("100");
        assertThat(from.getBalance()).isEqualByComparingTo("900");
        assertThat(to.getBalance()).isEqualByComparingTo("600");
        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
        verify(notificationService).notifyTransferCompleted(any(Transfer.class));
    }

    @Test
    void transfer_quandoMesmaConta_lancaBusinessException() {
        TransferRequest request = new TransferRequest(UUID_FROM, UUID_FROM, new BigDecimal("10"));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não podem ser iguais");
        verify(transferRepository, never()).save(any());
    }

    @Test
    void transfer_quandoSaldoInsuficiente_lancaBusinessException() {
        Account from = new Account("Maria", new BigDecimal("50"));
        from.setId(1L);
        from.setUuid(UUID_FROM);
        Account to = new Account("João", new BigDecimal("100"));
        to.setId(2L);
        to.setUuid(UUID_TO);
        when(accountRepository.findByUuid(UUID_FROM)).thenReturn(Optional.of(from));
        when(accountRepository.findByUuid(UUID_TO)).thenReturn(Optional.of(to));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        TransferRequest request = new TransferRequest(UUID_FROM, UUID_TO, new BigDecimal("100"));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Saldo insuficiente");
        verify(transferRepository, never()).save(any());
    }

    @Test
    void transfer_quandoContaOrigemNaoExiste_lancaResourceNotFoundException() {
        when(accountRepository.findByUuid(UUID_999)).thenReturn(Optional.empty());
        TransferRequest request = new TransferRequest(UUID_999, UUID_TO, new BigDecimal("10"));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("origem");
    }

    @Test
    void listByAccount_quandoContaExiste_retornaTransferencias() {
        Account from = new Account("A", BigDecimal.ONE);
        from.setId(1L);
        from.setUuid(UUID_FROM);
        Account to = new Account("B", BigDecimal.ONE);
        to.setId(2L);
        to.setUuid(UUID_TO);
        Transfer t = new Transfer(from, to, new BigDecimal("50"));
        t.setId(1L);
        t.setUuid(UUID_TRANSFER);
        when(accountRepository.findByUuid(UUID_ACCOUNT)).thenReturn(Optional.of(from));
        when(transferRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(t));

        var result = transferService.listByAccount(UUID_ACCOUNT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fromAccountUuid()).isEqualTo(UUID_FROM);
        assertThat(result.get(0).toAccountUuid()).isEqualTo(UUID_TO);
        verify(transferRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void listByAccount_quandoContaNaoExiste_lancaResourceNotFoundException() {
        when(accountRepository.findByUuid(UUID_999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.listByAccount(UUID_999))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
