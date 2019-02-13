package app.web.json;

import java.util.List;
import java.util.Map;

public class RequestData {

    private List<Long> integerList;
    private Map<String, ?> data;


    public List<Long> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Long> integerList) {
        this.integerList = integerList;
    }

    public Map<String, ?> getData() {
        return data;
    }

    public void setData(Map<String, ?> data) {
        this.data = data;
    }
}
