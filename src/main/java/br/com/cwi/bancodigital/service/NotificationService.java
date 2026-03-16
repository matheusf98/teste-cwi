package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.domain.Notification;
import br.com.cwi.bancodigital.domain.Transfer;
import br.com.cwi.bancodigital.repository.NotificationRepository;
import br.com.cwi.bancodigital.specification.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyTransferCompleted(Transfer transfer) {
        Account from = transfer.getFromAccount();
        Account to = transfer.getToAccount();
        BigDecimal amount = transfer.getAmount();

        String messageOut = String.format(
                "Transferência enviada: R$ %s para %s (conta %s).",
                amount, to.getName(), to.getUuid()
        );
        String messageIn = String.format(
                "Transferência recebida: R$ %s de %s (conta %s).",
                amount, from.getName(), from.getUuid()
        );

        notificationRepository.save(new Notification(transfer, from, messageOut));
        notificationRepository.save(new Notification(transfer, to, messageIn));
    }

    @Transactional(readOnly = true)
    public List<Notification> findByAccountUuid(UUID accountUuid) {
        var spec = NotificationSpecification.withAccountUuid(accountUuid);
        return notificationRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "sentAt"));
    }
}
