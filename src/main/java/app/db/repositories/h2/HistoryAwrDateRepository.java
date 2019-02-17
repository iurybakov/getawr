package app.db.repositories.h2;

import app.db.mappings.h2.ormt.HistoryAwrDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HistoryAwrDateRepository extends CrudRepository<HistoryAwrDate, Long> {

    @Query(value = "DELETE FROM ORA_AWR.HISTORY_AWR_DATE WHERE ROWNUM < 25", nativeQuery = true)
    Long clearHistory();
}
