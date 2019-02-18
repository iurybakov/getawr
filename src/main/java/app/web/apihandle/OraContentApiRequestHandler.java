package app.web.apihandle;

import app.web.json.Request;
import app.web.json.Response;

public interface OraContentApiRequestHandler {

    Response getContentOraMeta(final Request request);

    Response getContentOraUrl(final Request request);

    Response deleteOraUrl(final Request request);

    Response insertOraCredential(final Request request);

    Response updateOraCredential(final Request request);
}
