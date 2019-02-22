package app.db.repositories.ora;

import app.db.mappings.ora.DbaHistSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface OracleSnapRepository extends CrudRepository<DbaHistSnapshot, Long> {

    @Query(value = "SELECT FLR.SNAP_ID, FLR.DBID, FLR.INSTANCE_NUMBER, FLR.BEGIN_INTERVAL_TIME, FLR.END_INTERVAL_TIME FROM dba_hist_snapshot FLR JOIN (SELECT DBID, MIN(BEGIN_INTERVAL_TIME) AS PER_FROM, MAX(END_INTERVAL_TIME) AS PER_TO FROM dba_hist_snapshot GROUP BY DBID) MMR ON FLR.DBID = MMR.DBID AND FLR.BEGIN_INTERVAL_TIME = MMR.PER_FROM OR FLR.DBID = MMR.DBID AND FLR.END_INTERVAL_TIME = MMR.PER_TO ORDER BY FLR.DBID, FLR.BEGIN_INTERVAL_TIME", nativeQuery = true)
    List<DbaHistSnapshot> getMaxMinSnapPeriods();

    @Query(value = "SELECT FLR.SNAP_ID, FLR.DBID, FLR.INSTANCE_NUMBER, FLR.BEGIN_INTERVAL_TIME, FLR.END_INTERVAL_TIME FROM dba_hist_snapshot FLR JOIN (SELECT MIN(BEGIN_INTERVAL_TIME) AS PER_FROM, MAX(BEGIN_INTERVAL_TIME) AS PER_TO FROM dba_hist_snapshot WHERE DBID =?1 AND END_INTERVAL_TIME BETWEEN ?2 AND ?3) MMR ON FLR.BEGIN_INTERVAL_TIME = MMR.PER_FROM OR FLR.BEGIN_INTERVAL_TIME = MMR.PER_TO ORDER BY FLR.BEGIN_INTERVAL_TIME", nativeQuery = true)
    List<DbaHistSnapshot> findAllSnapPeriodsBydbIdAndBetween(Long dbId, Timestamp from, Timestamp to);
}

