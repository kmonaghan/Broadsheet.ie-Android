package ie.broadsheet.app.requests;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Comment;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.GenericData;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class MakeCommentRequest extends GoogleHttpClientSpiceRequest<Comment> {
    // private static final String TAG = "MakeCommentRequest";

    private String baseUrl;

    private int postId;

    private int commentId = 0;

    private String email;

    private String commentName;

    private String commentUrl;

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

    public String getCommentUrl() {
        return commentUrl;
    }

    public void setCommentUrl(String commentUrl) {
        this.commentUrl = commentUrl;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public MakeCommentRequest() {
        super(Comment.class);

        this.baseUrl = BroadsheetApplication.context().getString(R.string.apiURL) + "?json=respond.submit_comment";

    }

    @Override
    public Comment loadDataFromNetwork() throws Exception {
        GenericData data = new GenericData();
        data.put("post_id", postId);
        data.put("email", email);
        data.put("name", commentName);
        data.put("url", commentUrl);
        data.put("content", commentBody);

        if (commentId > 0) {
            data.put("comment_parent", commentId);
        }

        UrlEncodedContent content = new UrlEncodedContent(data);

        HttpRequest request = null;
        try {
            request = getHttpRequestFactory().buildPostRequest(new GenericUrl(baseUrl), content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }

}
