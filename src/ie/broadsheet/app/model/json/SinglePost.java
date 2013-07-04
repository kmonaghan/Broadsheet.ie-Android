package ie.broadsheet.app.model.json;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class SinglePost implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private String status;

    @Key
    private Post post;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "SinglePost [status=" + status + ", post=" + post + "]";
    }

}
