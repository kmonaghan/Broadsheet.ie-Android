package ie.broadsheet.app.requests;

import ie.broadsheet.app.model.json.Comment;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.GenericData;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class MakeCommentRequest extends GoogleHttpClientSpiceRequest<Comment> {
    private String baseUrl;

    private int postId;

    private String email;

    private String commentName;

    private String commentBody;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public MakeCommentRequest() {
        super(Comment.class);

        this.baseUrl = String.format("http://broadsheet.ie/?json=respond.submit_comment");

    }

    @Override
    public Comment loadDataFromNetwork() throws Exception {
        GenericData data = new GenericData();
        data.put("post_id", postId);
        data.put("email", email);
        data.put("name", commentName);
        data.put("content", commentBody);

        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), data);

        HttpRequest request = null;
        try {
            request = getHttpRequestFactory().buildPostRequest(new GenericUrl(baseUrl), content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setParser(new JacksonFactory().createJsonObjectParser());
        HttpHeaders headers = request.getHeaders();
        headers.setContentType("application/json; charset=UTF-8");

        return request.execute().parseAs(getResultType());
    }

}
