package app.db.utils;

import app.db.services.h2.H2Service;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class DataSourcesCache {


    private static final Logger LOG = LoggerFactory.getLogger(DataSourcesCache.class);

    private final LoadingCache<String, BasicDataSource> cache;


    public DataSourcesCache(final CacheBuilder cacheBuilder, final H2Service h2Service) {

        final CacheLoader<String, BasicDataSource> cacheLoader = new CacheLoader<String, BasicDataSource>() {
            @Override
            public BasicDataSource load(String id) throws Exception {
                return h2Service.createDataSourceByIdUrl(Long.parseLong(id));
            }
        };

        final RemovalListener<String, BasicDataSource> removalListener = removalNotification -> {
            try {
                removalNotification.getValue().close();
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        };
        LOG.info("-----------------------------------------_____________________________________((((((((((((**************************************&&&&&&&&&&&&&&&&&&&&&&&&&&");
        this.cache = cacheBuilder.removalListener(removalListener).build(cacheLoader);
    }

    public final BasicDataSource get(final String tennantId) {

        try {
            return cache.get(tennantId);

        } catch (ExecutionException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }


    public final boolean updateDataSource(final String tennantId) {

        cache.refresh(tennantId);
        return checkConnect(tennantId);
    }

    public final void removeDataSource(final String tennantId) {

        cache.invalidate(tennantId);
    }

    public final boolean checkConnect(final String tennantId) {

        try {
            cache.get(tennantId).getConnection().close();

        } catch (SQLException e) {

            LOG.error(e.getMessage());
            cache.invalidate(tennantId);
            return false;
        } catch (ExecutionException e) {

            LOG.error(e.getMessage());
            cache.invalidate(tennantId);
            return false;
        }

        OracleTenantIdentityResolver.setIdUrlConnectionToDB(tennantId);
        return true;
    }
}
