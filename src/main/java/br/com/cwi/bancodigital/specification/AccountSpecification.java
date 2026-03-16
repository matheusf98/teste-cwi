package br.com.cwi.bancodigital.specification;

import br.com.cwi.bancodigital.domain.Account;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Specifications para consultas dinâmicas na entidade Account.
 */
public final class AccountSpecification {

    private AccountSpecification() {
    }

    public static Specification<Account> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + name.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Account> balanceGreaterThanOrEqual(BigDecimal min) {
        if (min == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("balance"), min);
    }

    public static Specification<Account> balanceLessThanOrEqual(BigDecimal max) {
        if (max == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("balance"), max);
    }

    public static Specification<Account> withFilters(String name, BigDecimal minBalance, BigDecimal maxBalance) {
        return nameContains(name)
                .and(balanceGreaterThanOrEqual(minBalance))
                .and(balanceLessThanOrEqual(maxBalance));
    }
}
