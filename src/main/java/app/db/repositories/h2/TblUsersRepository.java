package app.db.repositories.h2;

import app.db.mappings.h2.sec.TblUsers;

public interface TblUsersRepository extends ContentRepository<TblUsers, Long> {

    Boolean existsByUsernameAndDeleted(final String username, final Boolean deleted);
}
