package app.web.handlers;

import app.db.services.apiservice.HistoryAwrJpaService;
import app.web.apihandle.AwrHistoryApiRequestHandler;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class AwrHistoryHandler implements AwrHistoryApiRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AwrHistoryHandler.class);
    private final HistoryAwrJpaService historyAwrH2Service;


    public AwrHistoryHandler(final HistoryAwrJpaService historyAwrH2Service) {

        this.historyAwrH2Service = historyAwrH2Service;
    }


    /*
    #
    # Get content on '/history' endpoint
    #
    */
    public final Response getContent(final Request request) {

        try {
            return Response.createSuccessResponse(request, historyAwrH2Service.getHistoryList());
        } catch (Exception ex) {
            LOG.error("handleHistoryGetContent", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    /*
    #
    # Get awr by click row on '/history' endpoint
    #
    */
    public final Response getAwr(final Request request) {

        try {
            if (request.getBody().getIntegerList() == null)
                return Response.createErrorResponse(request, "Error, field 'integerList' is null");

            final List<Long> credentialId = request.getBody().getIntegerList();

            final String report = historyAwrH2Service.getSavedAwr(credentialId.get(0));

            if (report == null)
                return Response.createErrorResponse(request, "Not found report");

            return Response.createSuccessResponse(request, report);
        } catch (Exception ex) {
            LOG.error("handleHistoryGetAwr", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }
}
