package app.web.json;

import java.util.List;
import java.util.Map;

public class ResponseData {

    private Map<String, String> properties;
    private List<?> data;


    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public List<?> getData() {
        return data;
    }

    public ResponseData setData(final List<?> data) {
        this.data = data;
        return this;
    }
}
