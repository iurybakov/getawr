package app.web.controller;

import app.db.services.h2.H2Service;
import app.db.services.ora.OracleService;
import app.db.utils.DataSourcesCache;
import app.web.json.Request;
import app.web.json.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class GetAwrRestController {

    @Autowired
    H2Service h2Service;

    @Autowired
    OracleService oracleService;

    @Autowired
    DataSourcesCache dataSourcesCache;

    @PostMapping(value = "/home", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response homeRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return handleHomeGetContentOraMetaRequest(request);
            case "periods":
                return handleGetOraclePeriodRequest(request);
            case "awr":
                return handleGetOracleAwrRequest(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }

    @PostMapping(value = "/history", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    String historyRest() {

        //dataSourceStorage.clear();
        return "{hello: \"hello\"}";
    }

    @PostMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response editRest(@RequestBody final Request request) {


        switch (request.getType()) {
            case "content":
                return handleGetEditContentOraUrlRequest(request);
            case "insert":
                return handleInsertOraCredentialRequest(request);
            case "update":
                return handleUpdateOraCredentialRequest(request);
            case "delete":
                return handleDeleteContentOraUrlRequest(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }

    @SuppressWarnings("unchecked")
    private Response handleGetOraclePeriodRequest(final Request request) {

        if (request.getBody().getIntegerList() == null || request.getBody().getIntegerList().size() == 0)
            return Response.createErrorResponse(request, "Error, field 'integerList' is null or empty");

        try {
            if (dataSourcesCache.checkConnect(String.valueOf(request.getBody().getIntegerList().get(0))))
                return Response.createErrorResponse(request, "Error, can't create connection");
            return Response.createSuccessResponse(request, oracleService.getPeriods());
        } catch (Exception ex) {
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Response handleGetOracleAwrRequest(final Request request) {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("filter") == null)
            return Response.createErrorResponse(request, "Error, not found field filter in data");

        final Map<String, String> filter = (Map<String, String>) data.get("filter");

        if (!compareFieldsByPattern(filter, "dateFrom", "dateTo", "dbId", "tennantId"))
            return Response.createErrorResponse(request, "Error, wrong set fields");

        try {
            if (dataSourcesCache.checkConnect(String.valueOf(filter.get("tennantId"))))
                return Response.createErrorResponse(request, "Error, can't create connection");
            return Response.createSuccessResponse(request, oracleService.getAwr(filter));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Response handleHomeGetContentOraMetaRequest(final Request request) {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("filter") == null)
            return Response.createErrorResponse(request, "Error, not found field filter in data");

        final Map<String, String> filter = (Map<String, String>) data.get("filter");

        if (!filter.keySet().containsAll(Arrays.asList("pageNumber", "countRowsPerPage")))
            return Response.createErrorResponse(request, "Error, not found filter criteria 'pageNumber' or 'countRowsPerPage'");

        final int pageNumber = Integer.parseInt(filter.remove("pageNumber"));
        final int countRowsPerPage = Integer.parseInt(filter.remove("countRowsPerPage"));

        if (!compareFieldsByPattern(filter, "name", "version", "os"))
            return Response.createErrorResponse(request, "Error, wrong set fields");

        return Response.createSuccessResponse(request, h2Service.getOraMetaResponseData(filter, PageRequest.of(pageNumber, countRowsPerPage)));
    }

    @SuppressWarnings("unchecked")
    private Response handleGetEditContentOraUrlRequest(final Request request) {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("filter") == null)
            return Response.createErrorResponse(request, "Error, not found field 'filter' in data");

        final Map<String, String> filter = (Map<String, String>) data.get("filter");

        if (!filter.keySet().containsAll(Arrays.asList("pageNumber", "countRowsPerPage")))
            return Response.createErrorResponse(request, "Error, not found filter criteria 'pageNumber' or 'countRowsPerPage'");

        final int pageNumber = Integer.parseInt(filter.remove("pageNumber"));
        final int countRowsPerPage = Integer.parseInt(filter.remove("countRowsPerPage"));

        if (!compareFieldsByPattern(filter, "name", "host", "port", "sid", "login"))
            return Response.createErrorResponse(request, "Error, wrong set fields");

        return Response.createSuccessResponse(request, h2Service.getOraUrlResponseData(filter, PageRequest.of(pageNumber, countRowsPerPage)));
    }

    @SuppressWarnings("unchecked")
    private Response handleDeleteContentOraUrlRequest(final Request request) {

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
    private Response handleInsertOraCredentialRequest(final Request request) {

        final String checkingError = checkCredentialForInsertOrUpdate(request, "name", "host", "port", "sid", "login", "pass");
        if (checkingError != null)
            return Response.createErrorResponse(request, checkingError);

        try {
            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(h2Service.insertNewOraCredential(credential));

            try {
                if (dataSourcesCache.checkConnect(credential.get("id")))
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                h2Service.updateOraMeta(credential);
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
//TODO повторяющийся код, сделать одним методом через передачу ссылки на метод insert или update (если получиться :) )
    @SuppressWarnings("unchecked")
    private Response handleUpdateOraCredentialRequest(final Request request) {

        final String checkingError = checkCredentialForInsertOrUpdate(request, "id", "name", "host", "port", "sid", "login", "pass");
        if (checkingError != null)
            return Response.createErrorResponse(request, checkingError);
        try {

            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(h2Service.updateOraCredential(credential));

            try {
                if (dataSourcesCache.updateDataSource(credential.get("id")))
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                h2Service.updateOraMeta(credential);
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
    private String checkCredentialForInsertOrUpdate(final Request request, final String... pattern) {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("credential") == null)
            return "Error, not found field 'credential' in data";
        final Map<String, String> credential = (Map<String, String>) data.get("credential");

        if (credential.size() != pattern.length)
            return "Error, wrong count fields";
        else if (!compareFieldsByPattern(credential, pattern))
            return "Error, wrong set fields";
        else if (!credential.get("port").matches("\\d+"))
            return "Error, field 'port' must have numeric value";
        else if (request.getType().equals("insert") && h2Service.checkNameExists(credential.get("name")))
            return "Error, not unique name";
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean compareFieldsByPattern(final Map<String, String> fields, String... pattern) {
        for (Map.Entry<String, String> entry : fields.entrySet())
            if (!Arrays.stream(pattern).anyMatch(patternMember -> patternMember.equals(entry.getKey()))
                    || entry.getValue() == null
                    || entry.getValue().isEmpty())
                return false;
        return true;
    }
}