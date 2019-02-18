package app.db.utils;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleTenantIdentityResolver implements CurrentTenantIdentifierResolver {

    private static final Logger LOG = LoggerFactory.getLogger(OracleTenantIdentityResolver.class);
    private static final ThreadLocal<String> ID_URL_DATABASE_CONNECTION = new ThreadLocal<>();

    static {
        ID_URL_DATABASE_CONNECTION.set(null);
    }

    public static String getIdUrlConnectionToDB() {
        return ID_URL_DATABASE_CONNECTION.get();
    }

    public static void setIdUrlConnectionToDB(final String tennantId) {

        LOG.info("setIdUrlConnectionToDB: ", tennantId);
        ID_URL_DATABASE_CONNECTION.set(tennantId);
    }

    @Override
    public String resolveCurrentTenantIdentifier() {

        LOG.info("resolveCurrentTenantIdentifier: ", ID_URL_DATABASE_CONNECTION.get());
        return ID_URL_DATABASE_CONNECTION.get() != null ? ID_URL_DATABASE_CONNECTION.get() : "0000001";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
