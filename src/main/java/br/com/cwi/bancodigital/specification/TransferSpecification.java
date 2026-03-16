package br.com.cwi.bancodigital.specification;

import br.com.cwi.bancodigital.domain.Transfer;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Specifications para consultas dinâmicas na entidade Transfer.
 */
public final class TransferSpecification {

    private TransferSpecification() {
    }

    /**
     * Filtra transferências em que a conta participou (origem ou destino) por UUID da conta.
     */
    public static Specification<Transfer> withAccountUuid(UUID accountUuid) {
        if (accountUuid == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("fromAccount", JoinType.LEFT);
                root.fetch("toAccount", JoinType.LEFT);
            }
            return cb.or(
                    cb.equal(root.get("fromAccount").get("uuid"), accountUuid),
                    cb.equal(root.get("toAccount").get("uuid"), accountUuid)
            );
        };
    }
}
