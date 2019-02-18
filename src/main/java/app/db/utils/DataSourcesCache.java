package app.db.utils;

import app.db.services.apiservice.ContentOraInfoJpaService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class DataSourcesCache {


    private static final Logger LOG = LoggerFactory.getLogger(DataSourcesCache.class);

    private final LoadingCache<String, DataSource> cache;

    @SuppressWarnings("unchecked")
    public DataSourcesCache(final CacheBuilder cacheBuilder, final ContentOraInfoJpaService contentService) {

        final CacheLoader<String, DataSource> cacheLoader = new CacheLoader<String, DataSource>() {
            @Override
            public DataSource load(String id) throws Exception {
                return contentService.createDataSource(Long.parseLong(id));
            }
        };

//        final RemovalListener<String, DriverManagerDataSource> removalListener = removalNotification -> {
//            try {
//                removalNotification.getValue().close();
//            } catch (SQLException e) {
//                LOG.error(e.getMessage());
//            }
//        };
                                /*removalListener(removalListener).*/
        this.cache = cacheBuilder.build(cacheLoader);
    }


    public final DataSource get(final String tennantId) {

        LOG.info("Get datasource: ", tennantId);
        try {
            return cache.get(tennantId);

        } catch (ExecutionException e) {
            LOG.error("get", e);
            return null;
        }
    }


    public final boolean updateDataSource(final String tennantId) {

        LOG.info("Update datasource: ", tennantId);
        cache.refresh(tennantId);
        return checkConnect(tennantId);
    }


    public final void removeDataSource(final String tennantId) {

        LOG.info("Remove datasource: ", tennantId);
        cache.invalidate(tennantId);
    }


    public final boolean checkConnect(final String tennantId) {

        LOG.info("Check connection datasource: ", tennantId);
        try {
            cache.get(tennantId).getConnection().close();
            LOG.info("Connected successful: ", tennantId);
        } catch (SQLException | ExecutionException e) {

            LOG.error("checkConnect", e);
            cache.invalidate(tennantId);
            return false;
        }

        OracleTenantIdentityResolver.setIdUrlConnectionToDB(tennantId);
        return true;
    }
}
