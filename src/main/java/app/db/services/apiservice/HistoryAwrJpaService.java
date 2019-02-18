package app.db.services.apiservice;

import app.web.json.ResponseData;

import java.util.Map;

public interface HistoryAwrJpaService {

    String getSavedAwr(final Map<String, Long> credentials);

    String getSavedAwr(final Long credentialId);

    ResponseData getHistoryList();

    void saveReport(final String dbName, final Map<String, Long> credentials, final String awrReport) throws Exception;
}
