package app.db.repositories.h2;

import app.db.mappings.h2.ormt.OraMeta;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;


public interface OraMetaRepository extends ContentRepository<OraMeta, Long>, JpaSpecificationExecutor<OraMeta> {
    @Nullable
    Long countByNameAndDeleted(final String name, final Boolean deleted);
}

