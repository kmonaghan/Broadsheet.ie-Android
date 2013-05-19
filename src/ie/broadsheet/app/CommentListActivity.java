package ie.broadsheet.app;

import ie.broadsheet.app.adapters.CommentAdapter;
import ie.broadsheet.app.model.json.Post;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;

public class CommentListActivity extends SherlockListActivity {
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            BroadsheetApplication app = (BroadsheetApplication) getApplication();

            post = app.getPosts().get(extras.getInt("item_id"));
        }

        setListAdapter(new CommentAdapter(this, R.layout.comment_list_item, post.getComments()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.comment_list, menu);
        return true;
    }

}
