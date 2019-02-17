package app.db.repositories.h2;

import app.db.mappings.h2.ormt.OraUrl;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;


public interface OraUrlRepository extends ContentRepository<OraUrl, Long>, JpaSpecificationExecutor<OraUrl> {
    @Nullable
    OraUrl findByIdAndDeleted(final Long id, final Boolean deleted);
}
