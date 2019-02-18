package app.web.apihandle;

import app.web.json.Request;
import app.web.json.Response;

public interface AwrHistoryApiRequestHandler {

    Response getContent(final Request request);

    Response getAwr(final Request request);
}
