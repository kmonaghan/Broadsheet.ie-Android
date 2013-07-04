package ie.broadsheet.app.model.json;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    @Key
    private String url;

    @Key
    private int width;

    @Key
    private int height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + width;
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
        Image other = (Image) obj;
        if (height != other.height)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Image [url=" + url + ", width=" + width + ", height=" + height + "]";
    }

}
