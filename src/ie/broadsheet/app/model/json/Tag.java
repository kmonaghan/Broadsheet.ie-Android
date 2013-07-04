package ie.broadsheet.app.model.json;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private int id;

    @Key
    private String slug;

    @Key
    private String title;

    @Key
    private String description;

    @Key
    private int post_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result + post_count;
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        Tag other = (Tag) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id != other.id)
            return false;
        if (post_count != other.post_count)
            return false;
        if (slug == null) {
            if (other.slug != null)
                return false;
        } else if (!slug.equals(other.slug))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tag [id=" + id + ", slug=" + slug + ", title=" + title + ", description=" + description
                + ", post_count=" + post_count + "]";
    }

}
