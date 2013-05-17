package ie.broadsheet.app.adapters;

import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Post;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostListAdapter extends ArrayAdapter<Post> {
    // private static final String TAG = "PostListAdapter";

    public static class ViewHolder {
        public TextView titleView;

        public TextView dateView;

        public TextView commentCountView;

        public ImageView featuredImage;
    }

    public PostListAdapter(Context context) {
        super(context, R.layout.activity_post_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        ViewHolder holder;
        if (v == null) {
            Activity activity = (Activity) getContext();

            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.post_list_item, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) v.findViewById(R.id.post_title);
            holder.dateView = (TextView) v.findViewById(R.id.post_date);
            holder.commentCountView = (TextView) v.findViewById(R.id.comment_count);
            holder.featuredImage = (ImageView) v.findViewById(R.id.featuredImage);
            v.setTag(holder);
        } else
            holder = (ViewHolder) v.getTag();

        final Post post = getItem(position);
        if (post != null) {

            holder.titleView.setText(Html.fromHtml(post.getTitle_plain()));
            holder.dateView.setText(post.getDate());
            holder.commentCountView.setText(post.getCommentCountString());

            String featuredImage = post.getFeaturedImage();

            if ((featuredImage != null) && (featuredImage.length() > 0)) {
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()

                .build();
                ImageLoader.getInstance().displayImage(featuredImage, holder.featuredImage, options);
            }
        }

        return v;
    }
}
