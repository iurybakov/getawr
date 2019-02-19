package app.web.json;

import java.util.Date;
import java.util.UUID;

public class Response {

    private String id = UUID.randomUUID().toString();
    private String requestId;
    private Date time = new Date();
    private String type;
    private Boolean success = true;
    private Object body;

    private Response() {
    }


    public static final Response createErrorResponse(final Request request, final String message) {
        final Response response = new Response();
        response.setRequestId(request.getId());
        response.setType(request.getType());
        response.success = false;
        response.body = message;
        return response;
    }


    public static final Response createSuccessResponse(final Request request, final Object body) {
        final Response response = new Response();
        response.setRequestId(request.getId());
        response.setType(request.getType());
        response.body = body;
        return response;
    }


    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(final Object body) {
        this.body = body;
    }
}
