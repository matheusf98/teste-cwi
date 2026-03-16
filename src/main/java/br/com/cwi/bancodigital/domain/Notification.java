package br.com.cwi.bancodigital.domain;

import br.com.cwi.bancodigital.constant.ColumnNames;
import br.com.cwi.bancodigital.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = TableNames.NOTIFICATIONS, indexes = @Index(columnList = ColumnNames.UUID, unique = true))
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = ColumnNames.UUID, nullable = false, unique = true, updatable = false, length = 36)
    private UUID uuid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnNames.TRANSFER_ID, nullable = false)
    private Transfer transfer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnNames.ACCOUNT_ID, nullable = false)
    private Account account;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, updatable = false)
    private Instant sentAt = Instant.now();

    @PrePersist
    protected void onPrePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Notification(Transfer transfer, Account account, String message) {
        this.transfer = transfer;
        this.account = account;
        this.message = message;
    }
}
