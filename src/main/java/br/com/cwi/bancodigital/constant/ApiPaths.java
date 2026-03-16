package br.com.cwi.bancodigital.constant;

/**
 * Constantes dos recursos e paths da API REST.
 */
public final class ApiPaths {

    public static final String API_BASE = "/api";

    public static final String RESOURCE_ACCOUNTS = "accounts";
    public static final String RESOURCE_TRANSFERS = "transfers";
    public static final String RESOURCE_NOTIFICATIONS = "notifications";

    public static final String PATH_ACCOUNTS = API_BASE + "/" + RESOURCE_ACCOUNTS;
    public static final String PATH_TRANSFERS = API_BASE + "/" + RESOURCE_TRANSFERS;
    public static final String PATH_NOTIFICATIONS = API_BASE + "/" + RESOURCE_NOTIFICATIONS;

    /** Parâmetro de path: UUID do recurso (conta, etc.). */
    public static final String PATH_PARAM_UUID = "uuid";
    public static final String PATH_PARAM_ACCOUNT_UUID = "accountUuid";

    public static final String SEGMENT_ACCOUNT = "account";
    public static final String SEGMENT_MOVEMENTS = "movements";

    private ApiPaths() {
    }
}
