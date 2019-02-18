package app.web.apihandle;

import app.web.json.Request;
import app.web.json.Response;

public interface AdminApiRequestHandler {

    Response insertUser(final Request request);

    Response getUsers(final Request request);

    Response operateUser(final Request request);
}
