package br.com.cwi.bancodigital.service;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.domain.Transfer;
import br.com.cwi.bancodigital.dto.MovementResponse;
import br.com.cwi.bancodigital.dto.TransferRequest;
import br.com.cwi.bancodigital.dto.TransferResponse;
import br.com.cwi.bancodigital.exception.BusinessException;
import br.com.cwi.bancodigital.exception.ResourceNotFoundException;
import br.com.cwi.bancodigital.repository.AccountRepository;
import br.com.cwi.bancodigital.repository.TransferRepository;
import br.com.cwi.bancodigital.specification.TransferSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final NotificationService notificationService;

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request.fromAccountUuid().equals(request.toAccountUuid())) {
            throw new BusinessException("Conta de origem e destino não podem ser iguais.");
        }

        Account from = accountRepository.findByUuid(request.fromAccountUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Conta de origem não encontrada: " + request.fromAccountUuid()));
        Account to = accountRepository.findByUuid(request.toAccountUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Conta de destino não encontrada: " + request.toAccountUuid()));

        from = accountRepository.findByIdForUpdate(from.getId()).orElseThrow();
        to = accountRepository.findByIdForUpdate(to.getId()).orElseThrow();

        BigDecimal amount = request.amount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente na conta de origem.");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);

        Transfer transfer = new Transfer(from, to, amount);
        transfer = transferRepository.save(transfer);

        notificationService.notifyTransferCompleted(transfer);

        return TransferResponse.from(transfer);
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> listByAccount(UUID accountUuid) {
        if (accountRepository.findByUuid(accountUuid).isEmpty()) {
            throw new ResourceNotFoundException("Conta não encontrada: " + accountUuid);
        }
        var spec = TransferSpecification.withAccountUuid(accountUuid);
        return transferRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(TransferResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> listMovementsByAccount(UUID accountUuid) {
        if (accountRepository.findByUuid(accountUuid).isEmpty()) {
            throw new ResourceNotFoundException("Conta não encontrada: " + accountUuid);
        }
        var spec = TransferSpecification.withAccountUuid(accountUuid);
        return transferRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .flatMap(t -> {
                    if (t.getFromAccount().getUuid().equals(accountUuid)) {
                        return List.of(new MovementResponse(
                                t.getUuid(), "SAIDA", t.getToAccount().getUuid(), t.getToAccount().getName(),
                                t.getAmount().negate(), t.getCreatedAt())).stream();
                    } else {
                        return List.of(new MovementResponse(
                                t.getUuid(), "ENTRADA", t.getFromAccount().getUuid(), t.getFromAccount().getName(),
                                t.getAmount(), t.getCreatedAt())).stream();
                    }
                })
                .toList();
    }
}
