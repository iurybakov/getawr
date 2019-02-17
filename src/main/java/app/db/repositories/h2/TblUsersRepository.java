package app.db.repositories.h2;

import app.db.mappings.h2.sec.TblUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

public interface TblUsersRepository extends ContentRepository<TblUsers, Long> {
}
