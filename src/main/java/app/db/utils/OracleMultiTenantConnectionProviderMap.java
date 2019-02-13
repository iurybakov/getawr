package app.db.utils;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import javax.sql.DataSource;


public class OracleMultiTenantConnectionProviderMap extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private final DataSourcesCache dataSourceCache;
    private final DataSource initDataSource;

    OracleMultiTenantConnectionProviderMap(final DataSource initDataSource, final DataSourcesCache dataSourceCache) {

        this.dataSourceCache = dataSourceCache;
        this.initDataSource = initDataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {

        return initDataSource;
    }

    @Override
    protected DataSource selectDataSource(final String tennantId) {

        return dataSourceCache.get(tennantId);
    }
}
