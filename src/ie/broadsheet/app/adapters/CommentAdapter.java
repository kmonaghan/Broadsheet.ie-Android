package ie.broadsheet.app.adapters;

import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Comment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentAdapter extends ArrayAdapter<Comment> implements
		View.OnClickListener {
	public interface ReplyCommentListener {
		public void onReply(int commentId);
	}

	ReplyCommentListener mListener;

	public static class ViewHolder {
		public ImageView commentAvatar;

		public TextView commentUser;

		public TextView commentDate;

		public TextView commentBody;

		public Button replyButton;
	}

	public void setReplyCommentListener(ReplyCommentListener mListener) {
		this.mListener = mListener;
	}

	public CommentAdapter(Context context, int textViewResourceId,
			List<Comment> comments) {
		super(context, textViewResourceId, comments);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		Activity activity = (Activity) getContext();

		ViewHolder holder;
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.comment_list_item, parent, false);
			holder = new ViewHolder();
			holder.commentAvatar = (ImageView) v
					.findViewById(R.id.commentAvatar);
			holder.commentUser = (TextView) v.findViewById(R.id.commentUser);
			holder.commentDate = (TextView) v.findViewById(R.id.commentDate);
			holder.commentBody = (TextView) v.findViewById(R.id.commentBody);
			holder.replyButton = (Button) v.findViewById(R.id.commentReply);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();

			holder.commentAvatar.setImageDrawable(activity.getResources()
					.getDrawable(R.drawable.default_user));
		}

		int standardMargin = activity.getResources().getDimensionPixelSize(
				R.dimen.standard_margin);

		final Comment comment = getItem(position);

		int newMargin = standardMargin
				+ (standardMargin * 2 * comment.getChildLevel());
		v.setPadding(newMargin, v.getPaddingTop(), v.getPaddingRight(),
				v.getPaddingBottom());

		if (comment != null) {
			String avatar = comment.getAvatar();

			if ((avatar != null) && (avatar.length() > 0)) {
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.cacheInMemory().cacheOnDisc()
						/*
						 * .displayer( new
						 * RoundedBitmapDisplayer(activity.getResources
						 * ().getDimensionPixelSize(
						 * R.dimen.standard_corner_radius)))
						 */
						.build();

				ImageLoader.getInstance().displayImage(avatar,
						holder.commentAvatar, options);
			}

			holder.commentUser.setText(comment.getName());
			holder.commentDate.setText(comment.getRelativeTime());
			holder.commentBody.setText(Html.fromHtml(comment.getContent()));

			holder.replyButton.setTag(position);
			holder.replyButton.setOnClickListener(this);
		}

		return v;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		final Comment comment = getItem(position);

		mListener.onReply(comment.getId());
	}
}
