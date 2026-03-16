package br.com.cwi.bancodigital.domain;

import br.com.cwi.bancodigital.constant.ColumnNames;
import br.com.cwi.bancodigital.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = TableNames.ACCOUNTS, indexes = @Index(columnList = ColumnNames.UUID, unique = true))
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = ColumnNames.UUID, nullable = false, unique = true, updatable = false, length = 36)
    private UUID uuid;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> transfersOut = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> transfersIn = new ArrayList<>();

    @PrePersist
    protected void onPrePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }
}
