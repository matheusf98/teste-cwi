package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.domain.Transfer;
import br.com.cwi.bancodigital.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final UUID UUID_ACCOUNT = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void notifyTransferCompleted_persisteDuasNotificacoes() {
        Account from = new Account("Maria", BigDecimal.ZERO);
        from.setId(1L);
        from.setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Account to = new Account("João", BigDecimal.ZERO);
        to.setId(2L);
        to.setUuid(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Transfer transfer = new Transfer(from, to, new BigDecimal("100"));
        transfer.setId(1L);
        transfer.setUuid(UUID.fromString("00000000-0000-0000-0000-000000000010"));
        when(notificationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        notificationService.notifyTransferCompleted(transfer);

        ArgumentCaptor<br.com.cwi.bancodigital.domain.Notification> captor =
                ArgumentCaptor.forClass(br.com.cwi.bancodigital.domain.Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        List<br.com.cwi.bancodigital.domain.Notification> saved = captor.getAllValues();
        assertThat(saved.get(0).getMessage()).contains("enviada").contains("João");
        assertThat(saved.get(1).getMessage()).contains("recebida").contains("Maria");
    }

    @Test
    void findByAccountUuid_retornaNotificacoesOrdenadas() {
        when(notificationRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        var result = notificationService.findByAccountUuid(UUID_ACCOUNT);

        assertThat(result).isEmpty();
        verify(notificationRepository).findAll(any(Specification.class), any(Sort.class));
    }
}
