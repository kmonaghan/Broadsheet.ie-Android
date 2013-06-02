package ie.broadsheet.app.requests;

import ie.broadsheet.app.model.json.PostList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class PostListRequest extends GoogleHttpClientSpiceRequest<PostList> {
    private String baseUrl;

    private int page = 1;

    private int count = 10;

    private String searchTerm;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public PostListRequest() {
        super(PostList.class);

        this.baseUrl = String.format("http://www.broadsheet.ie/?json=1");

    }

    @Override
    public PostList loadDataFromNetwork() throws Exception {
        Log.d("PostListRequest", "Call web service " + generateUrl());
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(generateUrl()));
        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

    public String generateUrl() {
        String generatedURL = this.baseUrl;

        if (page > 1) {
            generatedURL += "&page=" + Integer.toString(page);
        }

        generatedURL += "&count=" + Integer.toString(count);

        if ((searchTerm != null) && (searchTerm.length() > 0)) {
            try {
                generatedURL += "&s=" + URLEncoder.encode(searchTerm, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return generatedURL;
    }
}
