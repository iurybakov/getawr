package app.db.services.apiservice;

import app.web.json.ResponseData;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminJpaService {

    ResponseData getListUsers(final Pageable pageNum);

    ResponseData insertUser(final Map<String, String> credential, final Pageable pageNum) throws Exception;

    ResponseData operateUser(final Long id, final String action, final Pageable pageNum);
}
