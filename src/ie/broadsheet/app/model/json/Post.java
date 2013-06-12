package ie.broadsheet.app.model.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;

import com.google.api.client.util.Key;

public class Post {
    private static final String TAG = "Post";

    @Key
    private int id;

    @Key
    private String type;

    @Key
    private String slug;

    @Key
    private String url;

    @Key
    private String status;

    @Key
    private String title;

    @Key
    private String title_plain;

    @Key
    private String content;

    @Key
    private String excerpt;

    @Key
    private String date;

    @Key
    private String modified;

    @Key
    private List<Category> categories;

    @Key
    private List<Tag> tags;

    @Key
    private Author author;

    @Key
    private List<Comment> comments;

    private List<Comment> sortedComments = null;

    @Key
    private int comment_count;

    @Key
    private String comment_status;

    @Key
    private List<Attachments> attachments;

    private String relativeTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_plain() {
        return title_plain;
    }

    public void setTitle_plain(String title_plain) {
        this.title_plain = title_plain;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getComment_status() {
        return comment_status;
    }

    public void setComment_status(String comment_status) {
        this.comment_status = comment_status;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + comment_count;
        result = prime * result + ((comment_status == null) ? 0 : comment_status.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((excerpt == null) ? 0 : excerpt.hashCode());
        result = prime * result + id;
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((title_plain == null) ? 0 : title_plain.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Post other = (Post) obj;

        if (attachments == null) {
            if (other.attachments != null)
                return false;
        } else if (!attachments.equals(other.attachments))
            return false;

        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (categories == null) {
            if (other.categories != null)
                return false;
        } else if (!categories.equals(other.categories))
            return false;
        if (comment_count != other.comment_count)
            return false;
        if (comment_status == null) {
            if (other.comment_status != null)
                return false;
        } else if (!comment_status.equals(other.comment_status))
            return false;
        if (comments == null) {
            if (other.comments != null)
                return false;
        } else if (!comments.equals(other.comments))
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
        if (excerpt == null) {
            if (other.excerpt != null)
                return false;
        } else if (!excerpt.equals(other.excerpt))
            return false;
        if (id != other.id)
            return false;
        if (modified == null) {
            if (other.modified != null)
                return false;
        } else if (!modified.equals(other.modified))
            return false;
        if (slug == null) {
            if (other.slug != null)
                return false;
        } else if (!slug.equals(other.slug))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (title_plain == null) {
            if (other.title_plain != null)
                return false;
        } else if (!title_plain.equals(other.title_plain))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
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
        return "Post [id=" + id + ", type=" + type + ", slug=" + slug + ", url=" + url + ", status=" + status
                + ", title=" + title + ", title_plain=" + title_plain + ", content=" + content + ", excerpt=" + excerpt
                + ", date=" + date + ", modified=" + modified + ", categories=" + categories + ", tags=" + tags
                + ", author=" + author + ", comments=" + comments + ", comment_count=" + comment_count
                + ", comment_status=" + comment_status + ", attachments=" + attachments + "]";
    }

    public String getCommentCountString() {
        String comments;

        if (comment_count == 0) {
            comments = "No comments yet";
        } else if (comment_count == 1) {
            comments = "1 comment";
        } else {
            comments = Integer.toString(comment_count) + " comments";
        }
        return comments;
    }

    public String getFeaturedImage() {
        String imageUrl = null;

        if (attachments.size() > 0) {
            AttachmentItem image = (AttachmentItem) ((Attachments) attachments.get(0)).getImages();

            if (image != null) {
                Image thumbnail = image.getFull();

                if (thumbnail != null) {
                    imageUrl = thumbnail.getUrl();
                }
            }
        }

        return imageUrl;
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

    public List<Comment> getSortedComments() {
        if (sortedComments == null) {
            sortComments();
        }

        return sortedComments;
    }

    private void sortComments() {
        TreeMap<String, Comment> allCommentsMap = new TreeMap<String, Comment>();
        for (Iterator<Comment> iterator = comments.iterator(); iterator.hasNext();) {
            Comment item = (Comment) iterator.next();

            item.setChildComment(new TreeMap<String, Comment>());
            allCommentsMap.put(Integer.toString(item.getId()), item);
        }

        TreeMap<String, Comment> commentParents = new TreeMap<String, Comment>();

        TreeMap<String, Comment> allComments = new TreeMap<String, Comment>();

        SortedSet<String> keys = new TreeSet<String>(allCommentsMap.keySet());
        for (String key : keys) {
            Comment comment = allCommentsMap.get(key);

            if (comment.getParent() > 0) {
                Comment parentComment = allCommentsMap.get(Integer.toString(comment.getParent()));

                if (parentComment != null) {
                    if (parentComment.getChildComment().size() > 0) {
                        parentComment.getChildComment().put(key, comment);
                    } else {
                        TreeMap<String, Comment> childer = new TreeMap<String, Comment>();
                        childer.put(key, comment);
                        parentComment.setChildComment(childer);
                    }

                    comment.setChildLevel(parentComment.getChildLevel() + 1);

                    allComments.put(Integer.toString(parentComment.getId()), parentComment);
                } else {
                    commentParents.put(key, comment);
                }
            } else {
                commentParents.put(key, comment);
            }
        }

        sortedComments = new ArrayList<Comment>();

        SortedSet<String> parentkeys = new TreeSet<String>(allCommentsMap.keySet());
        for (String key : parentkeys) {
            Comment comment = commentParents.get(key);

            if (comment != null) {
                sortedComments.add(comment);

                if ((comment.getChildComment() != null) && (comment.getChildComment().size() > 0)) {
                    // sortedComments.addAll(flattenComments(comment.getChildComment()));
                    for (Comment addcomment : flattenComments(comment.getChildComment())) {
                        sortedComments.add(addcomment);
                    }
                }
            }
        }

    }

    public List<Comment> flattenComments(TreeMap<String, Comment> comments) {
        List<Comment> flattened = new ArrayList<Comment>();

        SortedSet<String> parentkeys = new TreeSet<String>(comments.keySet());
        for (String key : parentkeys) {
            Comment comment = comments.get(key);

            flattened.add(comment);

            if (comment.getChildComment().size() > 0) {
                flattened.addAll(flattenComments(comment.getChildComment()));
            }
        }

        return flattened;

    }

    public void addComment(Comment comment) {
        comments.add(comment);

        sortComments();
    }
}
