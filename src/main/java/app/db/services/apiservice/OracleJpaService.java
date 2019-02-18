package app.db.services.apiservice;

import app.web.json.ResponseData;

import java.util.Map;

public interface OracleJpaService {

    void setInMapOperatingSystemAndVersion(final Map<String, String> credentialOraMeta);

    ResponseData getPeriods() throws Exception;

    Map<String, Long> getSnapsId(Map<String, String> data) throws Exception;

    String getAwr(Map<String, Long> data) throws Exception;
}
