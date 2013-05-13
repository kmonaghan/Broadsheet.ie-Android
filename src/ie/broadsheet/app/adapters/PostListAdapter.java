package ie.broadsheet.app.adapters;

import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Post;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostListAdapter extends ArrayAdapter<Post> {
    private static final String TAG = "PostListAdapter";

    public static class ViewHolder {
        public TextView titleView;

        public TextView dateView;

        public TextView commentCountView;

        public ImageView featuredImage;
    }

    private final List<Post> posts;

    private int totalPosts;

    private View loadMoreFragment;

    public List<Post> getPosts() {
        return posts;
    }

    public PostListAdapter(Context context, List<Post> posts, int total) {
        super(context, R.layout.activity_post_list, posts);

        this.posts = posts;
        this.totalPosts = total;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (position < posts.size()) {
            ViewHolder holder;
            if (v == null) {
                Activity activity = (Activity) getContext();

                LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.post_list_item, null);
                holder = new ViewHolder();
                holder.titleView = (TextView) v.findViewById(R.id.post_title);
                holder.dateView = (TextView) v.findViewById(R.id.post_date);
                holder.commentCountView = (TextView) v.findViewById(R.id.comment_count);

                v.setTag(holder);
            } else
                holder = (ViewHolder) v.getTag();

            final Post post = posts.get(position);
            if (post != null) {

                holder.titleView.setText(Html.fromHtml(post.getTitle_plain()));
                holder.dateView.setText(post.getDate());
                holder.commentCountView.setText(post.getCommentCountString());

            }
        } else {
            Log.d(TAG, "Showing loadmore");

            if (loadMoreFragment == null) {
                LayoutInflater vi = (LayoutInflater) ((Activity) getContext())
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                loadMoreFragment = vi.inflate(R.layout.load_more_item, parent, false);
            }
            return loadMoreFragment;
        }

        return v;
    }

    @Override
    public int getCount() {
        int total = 0;

        total = posts.size();

        if (total < totalPosts) {
            total++;
        }

        Log.d(TAG, "Total returned = " + Integer.toString(total));
        return total;
    }
}
