package app.db.utils;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class OracleTenantIdentityResolver implements CurrentTenantIdentifierResolver {

    private static final ThreadLocal<String> ID_URL_DATABASE_CONNECTION = new ThreadLocal<>();
    private static final String ID_FOR_FIRST_INFO_DATASOURCE = "0000000001";

    static {
        ID_URL_DATABASE_CONNECTION.set(null);
    }

    public static final String getIdUrlConnectionToDB() {
        return ID_URL_DATABASE_CONNECTION.get();
    }

    public static final void setIdUrlConnectionToDB(final String tennantId) {
        ID_URL_DATABASE_CONNECTION.set(tennantId);
    }

    public static final String getIdForFirstInfoDatasource() {
        return ID_FOR_FIRST_INFO_DATASOURCE;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        return ID_URL_DATABASE_CONNECTION.get() == null ? ID_FOR_FIRST_INFO_DATASOURCE : ID_URL_DATABASE_CONNECTION.get();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
