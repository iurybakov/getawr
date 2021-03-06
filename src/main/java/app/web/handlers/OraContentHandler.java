package app.web.handlers;

import app.db.services.apiservice.ContentOraInfoJpaService;
import app.db.services.apiservice.OracleJpaService;
import app.db.utils.DataSourcesCache;
import app.web.apihandle.OraContentApiRequestHandler;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Map;
import static app.web.handlers.RequestChecker.*;

public class OraContentHandler implements OraContentApiRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OraContentHandler.class);


    private final ContentOraInfoJpaService contentService;
    private final OracleJpaService oracleService;
    private final DataSourcesCache dataSourcesCache;


    public OraContentHandler(final ContentOraInfoJpaService contentService, final OracleJpaService oracleService, final DataSourcesCache dataSourcesCache) {
        this.contentService = contentService;
        this.oracleService = oracleService;
        this.dataSourcesCache = dataSourcesCache;
    }


    /*
    #
    # Get content for home table on '/home' endpoints
    #
    */
    public final Response getContentOraMeta(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            compareFieldsByPattern(filter, "name", "version", "os");

            return Response.createSuccessResponse(request, contentService.getOraMeta(filter, pageRequest));
        } catch (Exception ex) {
            LOG.error("handleHomeGetContentOraMetaRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Get content for edit table on '/edit' endpoints (ROLE_USER and ROLE_ADMIN)
    #
    */
    public final Response getContentOraUrl(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            compareFieldsByPattern(filter, "name", "host", "port", "sid", "login");

            return Response.createSuccessResponse(request, contentService.getOraUrl(filter, pageRequest));
        } catch (Exception ex) {
            LOG.error("handleGetEditContentOraUrlRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Delete (hide) Oracle url connection on '/edit' endpoints (ROLE_USER and ROLE_ADMIN)
    #
    */
    public final Response deleteOraUrl(final Request request) {

        if (request.getBody().getIntegerList() == null)
            return Response.createErrorResponse(request, "Error, field 'integerList' is null");

        final List<Long> credentialId = request.getBody().getIntegerList();

        try {
            dataSourcesCache.removeDataSource(String.valueOf(credentialId));
            return Response.createSuccessResponse(request, contentService.deleteOraCredentials(credentialId));
        } catch (Exception ex) {
            LOG.error("handleDeleteContentOraUrlRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Add new Oracle url entry on '/edit' endpoints (ROLE_USER and ROLE_ADMIN)
    #
    */
    @SuppressWarnings("unchecked")
    public final Response insertOraCredential(final Request request) {

        try {

            checkCredentialForInsertOrUpdate(request, contentService, "name", "host", "port", "sid", "login", "pass");
            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(contentService.insertOraCredential(credential));

            try {
                if (dataSourcesCache.checkConnect(credential.get("id"))) {
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                    contentService.updateOraMeta(credential);
                }
            } catch (Exception ex) {
                status.append(", but '");
                status.append(ex.getMessage());
                LOG.error("handleInsertOraCredentialRequest");
                ex.printStackTrace();
            }

            return Response.createSuccessResponse(request, status.toString());
        } catch (Exception ex) {
            LOG.error("handleInsertOraCredentialRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Update exists Oracle url entry on '/edit' endpoints (ROLE_USER and ROLE_ADMIN)
    #
    */
    @SuppressWarnings("unchecked")
    public final Response updateOraCredential(final Request request) {

        try {
            checkCredentialForInsertOrUpdate(request, contentService, "id", "name", "host", "port", "sid", "login", "pass");

            final Map<String, String> credential = (Map<String, String>) request.getBody().getData().get("credential");

            final StringBuilder status = new StringBuilder(contentService.updateOraCredential(credential));

            try {
                if (dataSourcesCache.updateDataSource(credential.get("id"))) {
                    oracleService.setInMapOperatingSystemAndVersion(credential);
                    contentService.updateOraMeta(credential);
                }
            } catch (Exception ex) {
                status.append(", but '");
                status.append(ex.getMessage().subSequence(0, 50));
                status.append("...'");
                LOG.error("handleUpdateOraCredentialRequest inner catch", ex);
            }

            return Response.createSuccessResponse(request, status.toString());
        } catch (Exception ex) {
            LOG.error("handleUpdateOraCredentialRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }
}
