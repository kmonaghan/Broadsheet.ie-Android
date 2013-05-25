package ie.broadsheet.app.model.json;

import com.google.api.client.util.Key;

public class ResponseData {
    @Key
    private String message;

    @Override
    public String toString() {
        return "ResponseData [message=" + message + "]";
    }

}
