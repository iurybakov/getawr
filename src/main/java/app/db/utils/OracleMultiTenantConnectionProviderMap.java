package app.db.utils;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


public class OracleMultiTenantConnectionProviderMap extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(OracleMultiTenantConnectionProviderMap.class);

    private final DataSourcesCache dataSourceCache;
    private DataSource initDataSource;

    OracleMultiTenantConnectionProviderMap(final DataSource initDataSource, final DataSourcesCache dataSourceCache) {

        this.dataSourceCache = dataSourceCache;
        this.initDataSource = initDataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {

        LOG.info("selectAnyDataSource");
        final DataSource temp = initDataSource;
        initDataSource = null;
        return temp;
    }

    @Override
    protected DataSource selectDataSource(final String tennantId) {

        LOG.info("selectDataSource");
        return dataSourceCache.get(tennantId);
    }
}
