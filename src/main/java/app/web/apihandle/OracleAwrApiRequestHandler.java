package app.web.apihandle;

import app.web.json.Request;
import app.web.json.Response;

public interface OracleAwrApiRequestHandler {

    Response getMinMaxPeriod(final Request request);

    Response getAwr(final Request request);
}
