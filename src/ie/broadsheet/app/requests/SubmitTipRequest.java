package ie.broadsheet.app.requests;

import ie.broadsheet.app.model.json.SubmitTipResponse;

import java.io.IOException;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.GenericData;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class SubmitTipRequest extends GoogleHttpClientSpiceRequest<SubmitTipResponse> {
    private static final String TAG = "SubmitTipRequest";

    private String name;

    private String email;

    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SubmitTipRequest() {
        super(SubmitTipResponse.class);
    }

    @Override
    public SubmitTipResponse loadDataFromNetwork() throws Exception {
        GenericData data = new GenericData();

        data.put("name", name);
        data.put("email", email);
        data.put("message", message);

        UrlEncodedContent content = new UrlEncodedContent(data);

        Log.d(TAG, content.getData().toString());

        HttpRequest request = null;
        try {
            request = getHttpRequestFactory().buildPostRequest(new GenericUrl("http://broadsheet.ie/iphone_tip.php"),
                    content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setParser(new JacksonFactory().createJsonObjectParser());

        HttpResponse response = request.execute();

        Log.d(TAG, response.parseAsString());

        return response.parseAs(getResultType());
    }
}
