package app.web.handlers;

import app.db.services.apiservice.AdminJpaService;
import app.web.apihandle.AdminApiRequestHandler;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import static app.web.handlers.RequestChecker.*;


public class AdminHandler implements AdminApiRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AdminHandler.class);

    private final AdminJpaService adminAppH2Service;

    public AdminHandler(final AdminJpaService adminAppH2Service) {

        this.adminAppH2Service = adminAppH2Service;
    }


    /*
    #
    # Add new user on '/edit/admin' endpoints (it's able only with ROLE_ADMIN)
    #
    */
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
            LOG.error("insertUser", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Get content table on '/edit/admin' endpoints (it's able only with ROLE_ADMIN)
    #
    */
    public final Response getUsers(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);
            final PageRequest pageRequest = getPage(filter);

            return Response.createSuccessResponse(request, adminAppH2Service.getListUsers(pageRequest));
        } catch (Exception ex) {
            LOG.error("getUsers", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Disable or delete (hide) user on '/edit/admin' endpoints (it's able only with ROLE_ADMIN)
    #
    */
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
            LOG.error("operateUser", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }
}
