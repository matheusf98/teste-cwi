package br.com.cwi.bancodigital.domain;

import br.com.cwi.bancodigital.constant.ColumnNames;
import br.com.cwi.bancodigital.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = TableNames.TRANSFERS, indexes = @Index(columnList = ColumnNames.UUID, unique = true))
@Getter
@Setter
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = ColumnNames.UUID, nullable = false, unique = true, updatable = false, length = 36)
    private UUID uuid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnNames.FROM_ACCOUNT_ID, nullable = false)
    private Account fromAccount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnNames.TO_ACCOUNT_ID, nullable = false)
    private Account toAccount;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    protected void onPrePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }
}
