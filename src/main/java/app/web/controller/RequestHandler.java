package app.web.controller;

import app.db.services.h2.AdminAppH2Service;
import app.db.services.h2.H2Service;
import app.db.services.h2.HistoryAwrH2Service;
import app.db.services.ora.OracleService;
import app.db.utils.DataSourcesCache;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private H2Service h2Service;
    private OracleService oracleService;
    private DataSourcesCache dataSourcesCache;
    private AdminAppH2Service adminAppH2Service;
    private HistoryAwrH2Service historyAwrH2Service;


    public RequestHandler(final H2Service h2Service, final OracleService oracleService, final AdminAppH2Service adminAppH2Service, final HistoryAwrH2Service historyAwrH2Service, final DataSourcesCache dataSourcesCache) {
        this.h2Service = h2Service;
        this.oracleService = oracleService;
        this.dataSourcesCache = dataSourcesCache;
        this.adminAppH2Service = adminAppH2Service;
        this.historyAwrH2Service = historyAwrH2Service;
    }

    public final Response handleHistoryGetContent(final Request request) {

        try {
            return Response.createSuccessResponse(request, historyAwrH2Service.getHistoryList());
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }

    public final Response handleHistoryGetAwr(final Request request) {

        try {
            if (request.getBody().getIntegerList() == null)
                return Response.createErrorResponse(request, "Error, field 'integerList' is null");

            final List<Long> credentialId = request.getBody().getIntegerList();

            final char[] report = historyAwrH2Service.getSavedAwr(credentialId.get(0));

            if (report == null)
                return Response.createErrorResponse(request, "Not found report");

            return Response.createSuccessResponse(request, historyAwrH2Service.getHistoryList());
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public final Response insertUser(final Request request) {

        try {
            if (request.getBody().getData().get("credential") == null)
                return Response.createErrorResponse(request, "Error, not found field 'credential'");

            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");
            final PageRequest pageRequest = getPage(credential);

            compareFieldsByPattern(credential, "user", "pass", "role");

            return Response.createSuccessResponse(request, adminAppH2Service.insertUser(credential, pageRequest));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response getUsers(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            return Response.createSuccessResponse(request, adminAppH2Service.getListUsers(pageRequest));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public final Response operateUser(final Request request) {

        try {

            if (request.getBody().getData().get("credential") == null)
                return Response.createErrorResponse(request, "Error, not found field 'credential'");

            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");
            final PageRequest pageRequest = getPage(credential);

            compareFieldsByPattern(credential, "id", "action");

            return Response.createSuccessResponse(request, adminAppH2Service.operateUser(Long.parseLong(credential.get("id")), credential.get("action"), pageRequest));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response handleGetOraclePeriodRequest(final Request request) {

        if (request.getBody().getIntegerList() == null || request.getBody().getIntegerList().size() == 0)
            return Response.createErrorResponse(request, "Error, field 'integerList' is null or empty");

        try {
            if (!dataSourcesCache.checkConnect(String.valueOf(request.getBody().getIntegerList().get(0))))
                return Response.createErrorResponse(request, "Error, can't create connection");
            return Response.createSuccessResponse(request, oracleService.getPeriods());
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response handleGetOracleAwrRequest(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);

            strictCompareFieldsByPatter(filter, "dateFrom", "dateTo", "dbId", "dbName", "tennantId");

            if (!dataSourcesCache.checkConnect(String.valueOf(filter.get("tennantId"))))
                return Response.createErrorResponse(request, "Error, can't create connection");

            final Map<String, Long> stringLongMap = oracleService.getSnapsId(filter);
            char[] report = historyAwrH2Service.getSavedAwr(stringLongMap);

            if (report == null) {
                report = oracleService.getAwr(stringLongMap);
                historyAwrH2Service.saveReport(filter.get("dbName"), stringLongMap, report);
                return Response.createSuccessResponse(request, oracleService.getAwr(stringLongMap));
            }

            return Response.createSuccessResponse(request, report);

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response handleHomeGetContentOraMetaRequest(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            compareFieldsByPattern(filter, "name", "version", "os");

            return Response.createSuccessResponse(request, h2Service.getOraMetaResponseData(filter, pageRequest));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response handleGetEditContentOraUrlRequest(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            compareFieldsByPattern(filter, "name", "host", "port", "sid", "login");

            return Response.createSuccessResponse(request, h2Service.getOraUrlResponseData(filter, pageRequest));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response handleDeleteContentOraUrlRequest(final Request request) {

        if (request.getBody().getIntegerList() == null)
            return Response.createErrorResponse(request, "Error, field 'integerList' is null");

        final List<Long> credentialId = request.getBody().getIntegerList();

        try {
            dataSourcesCache.removeDataSource(String.valueOf(credentialId));
            return Response.createSuccessResponse(request, h2Service.deleteOraCredentials(credentialId));
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getLocalizedMessage().substring(0, 70) + "...");
        }
    }


    @SuppressWarnings("unchecked")
    public final Response handleInsertOraCredentialRequest(final Request request) {

        try {

            checkCredentialForInsertOrUpdate(request, "name", "host", "port", "sid", "login", "pass");
            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(h2Service.insertNewOraCredential(credential));

            try {
                if (dataSourcesCache.checkConnect(credential.get("id"))) {
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                    h2Service.updateOraMeta(credential);
                }
            } catch (Exception ex) {
                status.append(", but '");
                status.append(ex.getMessage().subSequence(0, 50));
                status.append("...'");
                ex.printStackTrace();
            }

            return Response.createSuccessResponse(request, status.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.createErrorResponse(request, ex.getLocalizedMessage().substring(0, 70) + "...");
        }
    }


    @SuppressWarnings("unchecked")
    public final Response handleUpdateOraCredentialRequest(final Request request) {

        try {
            checkCredentialForInsertOrUpdate(request, "id", "name", "host", "port", "sid", "login", "pass");

            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(h2Service.updateOraCredential(credential));

            try {
                if (dataSourcesCache.updateDataSource(credential.get("id"))) {
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                    h2Service.updateOraMeta(credential);
                }
            } catch (Exception ex) {
                status.append(", but '");
                status.append(ex.getMessage().subSequence(0, 50));
                status.append("...'");
                ex.printStackTrace();
            }

            return Response.createSuccessResponse(request, status.toString());
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getLocalizedMessage().substring(0, 70) + "...");
        }
    }


    @SuppressWarnings("unchecked")
    private final void checkCredentialForInsertOrUpdate(final Request request, final String... pattern) throws Exception {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("credential") == null)
            throw new Exception("Error, not found field 'credential' in data");
        final Map<String, String> credential = (Map<String, String>) data.get("credential");

        if (credential.size() != pattern.length)
            throw new Exception("Error, wrong count fields");

        compareFieldsByPattern(credential, pattern);

        if (credential.get("port") != null && !credential.get("port").matches("\\d+"))
            throw new Exception("Error, field 'port' must have numeric value");
        else if (request.getType().equals("insert") && h2Service.checkNameExists(credential.get("name")))
            throw new Exception("Error, not unique name");
    }


    private final void compareFieldsByPattern(final Map<String, String> fields, String... pattern) throws Exception {

        for (Map.Entry<String, String> entry : fields.entrySet())
            if (Arrays.stream(pattern).noneMatch(patternMember -> patternMember.equals(entry.getKey()))
                    || entry.getValue() == null
                    || entry.getValue().isEmpty())
                throw new Exception("Error, wrong set fields");
    }


    private final void strictCompareFieldsByPatter(final Map<String, String> fields, String... pattern) throws Exception {

        if (fields.size() != pattern.length)
            throw new Exception("Error, wrong count fields");

        compareFieldsByPattern(fields, pattern);
    }


    private final PageRequest getPage(final Map<String, String> mapWithPage) throws Exception {

        if (!mapWithPage.keySet().containsAll(Arrays.asList("pageNumber", "countRowsPerPage")))
            throw new Exception("Error, not found filter criteria 'pageNumber' or 'countRowsPerPage'");

        final int pageNumber = Integer.parseInt(mapWithPage.remove("pageNumber"));
        final int countRowsPerPage = Integer.parseInt(mapWithPage.remove("countRowsPerPage"));

        return PageRequest.of(pageNumber, countRowsPerPage);
    }


    @SuppressWarnings("unchecked")
    private final Map<String, String> getFilter(final Request request) throws Exception {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("filter") == null)
            throw new Exception("Error, not found field 'filter' in data");

        return (Map<String, String>) data.get("filter");
    }
}