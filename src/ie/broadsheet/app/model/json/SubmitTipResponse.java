package ie.broadsheet.app.model.json;

import java.util.List;

import com.google.api.client.util.Key;

public class SubmitTipResponse {
    @Key
    private int status;

    @Key
    private List<ResponseData> data;

    @Override
    public String toString() {
        return "SubmitTipResponse [status=" + status + ", data=" + data + "]";
    }

}
