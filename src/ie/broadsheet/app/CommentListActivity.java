package ie.broadsheet.app;

import ie.broadsheet.app.adapters.CommentAdapter;
import ie.broadsheet.app.dialog.MakeCommentDialog;
import ie.broadsheet.app.model.json.Comment;
import ie.broadsheet.app.model.json.Post;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class CommentListActivity extends BaseFragmentActivity implements MakeCommentDialog.CommentMadeListener,
        CommentAdapter.ReplyCommentListener {
    private static final String TAG = "CommentListActivity";

    public static final String CURRENT_POST = "current_post";

    private Post mPost;

    private CommentAdapter comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        if (savedInstanceState != null) {
            Log.d(TAG, "saved instance");
            mPost = (Post) savedInstanceState.getSerializable(CURRENT_POST);
        } else {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                BroadsheetApplication app = (BroadsheetApplication) getApplication();

                if (app.getPosts().size() > 0) {
                    mPost = app.getPosts().get(extras.getInt("item_id"));
                }
            }
        }

        ListView list = (ListView) findViewById(android.R.id.list);

        comments = new CommentAdapter(this, R.layout.comment_list_item, mPost.getSortedComments());
        comments.setReplyCommentListener(this);

        list.setAdapter(comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll,
                pauseOnFling);
        list.setOnScrollListener(listener);

        setTitle(getResources().getString(R.string.comment));
    }

    @Override
    public void onStart() {
        super.onStart();

        ((BroadsheetApplication) getApplication()).getTracker().sendView(
                "Comment List: " + Html.fromHtml(mPost.getTitle_plain()) + " " + Integer.toString(mPost.getId()));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mPost.getComment_status().equals("open")) {
            getSupportMenuInflater().inflate(R.menu.comment_list, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_make_comment) {
            onReply(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCommentMade(Comment comment) {
        mPost.addComment(comment);

        comments.clear();
        // comments.addAll(mPost.getSortedComments());
        for (Comment addcomment : mPost.getSortedComments()) {
            comments.add(addcomment);
        }
        comments.notifyDataSetChanged();
    }

    @Override
    public void onReply(int commentId) {
        MakeCommentDialog dialog = new MakeCommentDialog();
        dialog.setPostId(mPost.getId());
        dialog.setCommentMadeListener(this);
        dialog.setCommentId(commentId);
        dialog.show(this.getSupportFragmentManager(), "MakeCommentDialog");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "saving instance");

        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(CURRENT_POST, mPost);
    }
}
