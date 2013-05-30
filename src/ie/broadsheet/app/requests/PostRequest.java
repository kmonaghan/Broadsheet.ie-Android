package ie.broadsheet.app.requests;

import ie.broadsheet.app.model.json.SinglePost;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class PostRequest extends GoogleHttpClientSpiceRequest<SinglePost> {
    private static final String TAG = "PostRequest";

    private String url;

    public PostRequest(String url) {
        super(SinglePost.class);

        this.url = url + "?json=1";

    }

    @Override
    public SinglePost loadDataFromNetwork() throws Exception {
        Log.d(TAG, url);

        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(url));
        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }
}
