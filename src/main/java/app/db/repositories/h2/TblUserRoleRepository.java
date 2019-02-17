package app.db.repositories.h2;

import app.db.mappings.h2.sec.TblUserRole;
import org.springframework.data.repository.CrudRepository;

public interface TblUserRoleRepository extends CrudRepository<TblUserRole, Long> {
}
