package br.com.cwi.bancodigital.constant;

/**
 * Nomes de colunas FK e campos reutilizados nas entidades.
 */
public final class ColumnNames {

    public static final String FROM_ACCOUNT_ID = "from_account_id";
    public static final String TO_ACCOUNT_ID = "to_account_id";
    public static final String TRANSFER_ID = "transfer_id";
    public static final String ACCOUNT_ID = "account_id";

    /** Coluna UUID para exposição na API (todas as entidades). */
    public static final String UUID = "uuid";

    private ColumnNames() {
    }
}
