package br.com.cwi.bancodigital.specification;

import br.com.cwi.bancodigital.domain.Notification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Specifications para consultas dinâmicas na entidade Notification.
 */
public final class NotificationSpecification {

    private NotificationSpecification() {
    }

    /**
     * Filtra notificações pela conta (UUID) e aplica fetch de transfer e account para evitar N+1.
     */
    public static Specification<Notification> withAccountUuid(UUID accountUuid) {
        if (accountUuid == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("transfer", JoinType.LEFT);
                root.fetch("account", JoinType.LEFT);
            }
            return cb.equal(root.get("account").get("uuid"), accountUuid);
        };
    }
}
