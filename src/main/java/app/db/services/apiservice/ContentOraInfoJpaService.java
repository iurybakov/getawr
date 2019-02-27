package app.db.services.apiservice;

import app.web.json.ResponseData;
import org.springframework.data.domain.Pageable;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public interface ContentOraInfoJpaService {

    ResponseData getOraMeta(final Map<String, String> filter, final Pageable pageNum);

    DataSource createDataSource(final Long id) throws Exception;

    ResponseData getOraUrl(final Map<String, String> filter, final Pageable pageNum);

    String insertOraCredential(final Map<String, String> credential);

    String updateOraCredential(final Map<String, String> credential) throws Exception;

    void updateOraMeta(final Map<String, String> credential) throws Exception;

    String deleteOraCredentials(final List<Long> credentialId);

    boolean checkNameExists(final String name);
}
