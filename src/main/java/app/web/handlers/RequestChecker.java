package app.web.handlers;

import app.db.services.apiservice.ContentOraInfoJpaService;
import app.web.json.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import java.util.Arrays;
import java.util.Map;

class RequestChecker {

    private static final Logger LOG = LoggerFactory.getLogger(RequestChecker.class);


    /*
    #
    # Check json data for corrected operation insert or update on '/edit' endpoints (ROLE_USER and ROLE_ADMIN)
    #
    */
    @SuppressWarnings("unchecked")
    protected static void checkCredentialForInsertOrUpdate(final Request request, final ContentOraInfoJpaService h2Service, final String... pattern) throws Exception {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("credential") == null)
            throw new Exception("Error, not found field 'credential' in data");
        final Map<String, String> credential = (Map<String, String>) data.get("credential");

        if (credential.size() != pattern.length)
            throw new Exception("Error, wrong count fields");

        strictCompareFieldsByPatter(credential, pattern);

        if (credential.get("port") != null && !credential.get("port").matches("\\d+"))
            throw new Exception("Error, field 'port' must have numeric value");
        else if (request.getType().equals("insert") && h2Service.checkNameExists(credential.get("name")))
            throw new Exception("Error, not unique name");
    }


    /*
    #
    # Check map keys by pattern and check map values on isn't empty
    #
    */
    protected static void compareFieldsByPattern(final Map<String, String> fields, final String... pattern) throws Exception {

        for (Map.Entry<String, String> entry : fields.entrySet())
            if (Arrays.stream(pattern).noneMatch(patternMember -> patternMember.equals(entry.getKey()))
                    || entry.getValue() == null
                    || entry.getValue().isEmpty())
                throw new Exception("Error, wrong set fields");
    }


    /*
    #
    # Check map keys by pattern and check map values on isn't empty. Also check equal count map and pattern elements
    #
    */
    protected static void strictCompareFieldsByPatter(final Map<String, String> fields, final String... pattern) throws Exception {

        if (fields.size() != pattern.length)
            throw new Exception("Error, wrong count fields");

        compareFieldsByPattern(fields, pattern);
    }


    /*
    #
    # Create PageRequest from map (filter)
    #
    */
    protected static PageRequest getPage(final Map<String, String> mapWithPage) throws Exception {

        if (!mapWithPage.keySet().containsAll(Arrays.asList("pageNumber", "countRowsPerPage")))
            throw new Exception("Error, not found filter criteria 'pageNumber' or 'countRowsPerPage'");

        final int pageNumber = Integer.parseInt(mapWithPage.remove("pageNumber"));
        final int countRowsPerPage = Integer.parseInt(mapWithPage.remove("countRowsPerPage"));

        return PageRequest.of(pageNumber, countRowsPerPage);
    }


    /*
    #
    # Check exists and get map data from request by key filter
    #
    */
    @SuppressWarnings("unchecked")
    protected static Map<String, String> getFilter(final Request request) throws Exception {

        final Map<String, ?> data = request.getBody().getData();

        if (data.get("filter") == null)
            throw new Exception("Error, not found field 'filter' in data");

        return (Map<String, String>) data.get("filter");
    }
}
