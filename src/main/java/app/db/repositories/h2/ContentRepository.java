package app.db.repositories.h2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

@NoRepositoryBean
public interface ContentRepository<T, ID extends Long> extends PagingAndSortingRepository<T, Long> {
    @Nullable
    Page<T> findByDeleted(Boolean deleted, Pageable pageable);
}
