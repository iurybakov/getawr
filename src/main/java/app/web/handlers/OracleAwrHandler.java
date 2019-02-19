package app.web.handlers;

import app.db.services.apiservice.HistoryAwrJpaService;
import app.db.services.apiservice.OracleJpaService;
import app.db.utils.DataSourcesCache;
import app.web.apihandle.OracleAwrApiRequestHandler;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import static app.web.handlers.RequestChecker.*;

public class OracleAwrHandler implements OracleAwrApiRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OracleAwrHandler.class);

    private final OracleJpaService oracleService;
    private final DataSourcesCache dataSourcesCache;
    private final HistoryAwrJpaService historyAwrH2Service;


    public OracleAwrHandler(final OracleJpaService oracleService, final HistoryAwrJpaService historyAwrH2Service, final DataSourcesCache dataSourcesCache) {

        this.oracleService = oracleService;
        this.dataSourcesCache = dataSourcesCache;
        this.historyAwrH2Service = historyAwrH2Service;
    }


    public final Response getMinMaxPeriod(final Request request) {

        if (request.getBody().getIntegerList() == null || request.getBody().getIntegerList().size() == 0)
            return Response.createErrorResponse(request, "Error, field 'integerList' is null or empty");

        try {
            if (!dataSourcesCache.checkConnect(String.valueOf(request.getBody().getIntegerList().get(0))))
                return Response.createErrorResponse(request, "Error, unable create connection");
            return Response.createSuccessResponse(request, oracleService.getPeriods());
        } catch (Exception ex) {
            LOG.error("handleGetOraclePeriodRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }


    public final Response getAwr(final Request request) {

        try {
            final Map<String, String> filter = getFilter(request);

            strictCompareFieldsByPatter(filter, "dateFrom", "dateTo", "dbId", "dbName", "tennantId");

            if (!dataSourcesCache.checkConnect(filter.get("tennantId")))
                return Response.createErrorResponse(request, "Error, unable create connection");

            final Map<String, Long> stringLongMap = oracleService.getSnapsId(filter);

            String report = historyAwrH2Service.getSavedAwr(stringLongMap);

            if (report == null) {
                report = oracleService.getAwr(stringLongMap);
                historyAwrH2Service.saveReport(filter.get("dbName"), stringLongMap, report);
                return Response.createSuccessResponse(request, oracleService.getAwr(stringLongMap));
            }


            return Response.createSuccessResponse(request, report);
        } catch (Exception ex) {
            LOG.error("handleGetOracleAwrRequest", ex);
            return Response.createErrorResponse(request, ex.getMessage());
        }
    }
}
