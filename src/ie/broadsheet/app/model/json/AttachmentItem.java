package ie.broadsheet.app.model.json;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class AttachmentItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private Image full;

    @Key
    private Image thumbnail;

    @Key
    private Image medium;

    public Image getFull() {
        return full;
    }

    public void setFull(Image full) {
        this.full = full;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getMedium() {
        return medium;
    }

    public void setMedium(Image medium) {
        this.medium = medium;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((full == null) ? 0 : full.hashCode());
        result = prime * result + ((medium == null) ? 0 : medium.hashCode());
        result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
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
        AttachmentItem other = (AttachmentItem) obj;
        if (full == null) {
            if (other.full != null)
                return false;
        } else if (!full.equals(other.full))
            return false;
        if (medium == null) {
            if (other.medium != null)
                return false;
        } else if (!medium.equals(other.medium))
            return false;
        if (thumbnail == null) {
            if (other.thumbnail != null)
                return false;
        } else if (!thumbnail.equals(other.thumbnail))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AttachmentItem [full=" + full + ", thumbnail=" + thumbnail + ", medium=" + medium + "]";
    }

}
