package app.db.repositories.h2;

import app.db.mappings.h2.OraUrl;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;


public interface OraUrlRepository extends OraInfoRepository<OraUrl, Long>, JpaSpecificationExecutor<OraUrl> {
    @Nullable
    OraUrl findByIdAndDeleted(final Long id, final Boolean deleted);
}
