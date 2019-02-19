package app.web.json;

import java.util.Date;

public class Request {

    private String id;
    private Date time;
    private String type;
    private RequestData body;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public RequestData getBody() {
        return body;
    }

    public void setBody(RequestData body) {
        this.body = body;
    }
}
