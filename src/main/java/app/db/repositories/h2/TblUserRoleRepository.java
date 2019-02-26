package app.db.repositories.h2;

import app.db.mappings.h2.sec.TblUserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;

public interface TblUserRoleRepository extends CrudRepository<TblUserRole, Long> {

    @Nullable
    TblUserRole findByRolename(final String rolename);
}
