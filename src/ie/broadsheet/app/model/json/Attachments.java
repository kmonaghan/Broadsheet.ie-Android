package ie.broadsheet.app.model.json;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class Attachments implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private int id;

    @Key
    private String url;

    @Key
    private String slug;

    @Key
    private String title;

    @Key
    private String description;

    @Key
    private String caption;

    @Key
    private int parent;

    @Key
    private String mime_type;

    @Key
    private AttachmentItem images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public AttachmentItem getImages() {
        return images;
    }

    public void setImages(AttachmentItem images) {
        this.images = images;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caption == null) ? 0 : caption.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result + ((images == null) ? 0 : images.hashCode());
        result = prime * result + ((mime_type == null) ? 0 : mime_type.hashCode());
        result = prime * result + parent;
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        Attachments other = (Attachments) obj;
        if (caption == null) {
            if (other.caption != null)
                return false;
        } else if (!caption.equals(other.caption))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id != other.id)
            return false;
        if (images == null) {
            if (other.images != null)
                return false;
        } else if (!images.equals(other.images))
            return false;
        if (mime_type == null) {
            if (other.mime_type != null)
                return false;
        } else if (!mime_type.equals(other.mime_type))
            return false;
        if (parent != other.parent)
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
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Attachments [id=" + id + ", url=" + url + ", slug=" + slug + ", title=" + title + ", description="
                + description + ", caption=" + caption + ", parent=" + parent + ", mime_type=" + mime_type
                + ", images=" + images + "]";
    }

}
