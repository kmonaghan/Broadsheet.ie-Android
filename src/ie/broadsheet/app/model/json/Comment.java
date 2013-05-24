package ie.broadsheet.app.model.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;

import com.google.api.client.util.Key;

public class Comment {
    @Key
    private int id;

    @Key
    private String name;

    @Key
    private String url;

    @Key
    private String date;

    @Key
    private String content;

    @Key
    private int parent;

    @Key
    private String avatar;

    @Key
    private String status;

    private String relativeTime;

    private TreeMap<String, Comment> childComment;

    private int childLevel = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TreeMap<String, Comment> getChildComment() {
        return childComment;
    }

    public void setChildComment(TreeMap<String, Comment> childComment) {
        this.childComment = childComment;
    }

    public int getChildLevel() {
        return childLevel;
    }

    public void setChildLevel(int childLevel) {
        this.childLevel = childLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + parent;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        Comment other = (Comment) obj;
        if (avatar == null) {
            if (other.avatar != null)
                return false;
        } else if (!avatar.equals(other.avatar))
            return false;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parent != other.parent)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
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
        return "Comment [id=" + id + ", name=" + name + ", url=" + url + ", date=" + date + ", content=" + content
                + ", parent=" + parent + ", avatar=" + avatar + ", status=" + status + "]";
    }

    @SuppressLint("SimpleDateFormat")
    public String getRelativeTime() {
        if (relativeTime == null) {
            relativeTime = "";
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date result = null;
            try {
                result = df.parse(this.date);
                relativeTime = (String) DateUtils.getRelativeTimeSpanString(result.getTime(), new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return relativeTime;
    }
}
