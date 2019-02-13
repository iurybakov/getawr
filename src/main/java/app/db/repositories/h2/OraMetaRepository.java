package app.db.repositories.h2;

import app.db.mappings.h2.OraMeta;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;


public interface OraMetaRepository extends OraInfoRepository<OraMeta, Long>, JpaSpecificationExecutor<OraMeta> {
    @Nullable
    Long countByNameAndDeleted(final String name, final Boolean deleted);
}

