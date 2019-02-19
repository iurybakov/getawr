package app.db.repositories.h2;

import app.db.mappings.h2.ormt.HistoryAwr;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;

public interface HistoryAwrRepository extends CrudRepository<HistoryAwr, Long> {

    @Nullable
    List<HistoryAwr> findAllByTennantIdAndSnapFromAndSnapTo(final Long tennantId, final Long snapFrom, final Long snapTo);

    Long countByTennantIdAndSnapFromAndSnapTo(final Long tennantId, final Long snapFrom, final Long snapTo);
}
