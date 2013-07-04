package ie.broadsheet.app.model.json;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

public class PostList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private String status;

    @Key
    private int count;

    @Key
    private int count_total;

    @Key
    private int pages;

    @Key
    private List<Post> posts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount_total() {
        return count_total;
    }

    public void setCount_total(int count_total) {
        this.count_total = count_total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count;
        result = prime * result + count_total;
        result = prime * result + pages;
        result = prime * result + ((posts == null) ? 0 : posts.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostList other = (PostList) obj;
        if (count != other.count)
            return false;
        if (count_total != other.count_total)
            return false;
        if (pages != other.pages)
            return false;
        if (posts == null) {
            if (other.posts != null)
                return false;
        } else if (!posts.equals(other.posts))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PostList [status=" + status + ", count=" + count + ", count_total=" + count_total + ", pages=" + pages
                + ", posts=" + posts + "]";
    }

}
