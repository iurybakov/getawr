package app.db.repositories.h2;

import app.db.mappings.h2.ormt.HistoryAwrDate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HistoryAwrDateRepository extends CrudRepository<HistoryAwrDate, Long> {

    @Modifying
    @Query(value = "DELETE FROM ORA_AWR.HISTORY_AWR_DATE WHERE ROWNUM < 5", nativeQuery = true)
    Integer clearHistory();
}
