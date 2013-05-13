package ie.broadsheet.app.model.json;

import com.google.api.client.util.Key;

public class AttachmentItem {
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

}
