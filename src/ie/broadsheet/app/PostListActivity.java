package ie.broadsheet.app;

import ie.broadsheet.app.fragments.PostDetailFragment;
import ie.broadsheet.app.fragments.PostListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * An activity representing a list of Posts. This activity has different presentations for handset and tablet-size
 * devices. On handsets, the activity presents a list of items, which when touched, lead to a {@link PostDetailActivity}
 * representing item details. On tablets, the activity presents the list of items and item details side-by-side using
 * two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a {@link PostListFragment} and the item details (if
 * present) is a {@link PostDetailFragment}.
 * <p>
 * This activity also implements the required {@link PostListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class PostListActivity extends BaseFragmentActivity implements PostListFragment.Callbacks {
    private static final String TAG = "PostListActivity";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        if (findViewById(R.id.post_detail_container) != null) {
            Log.d(TAG, "Dual screen");
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((PostListFragment) getSupportFragmentManager().findFragmentById(R.id.post_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link PostListFragment.Callbacks} indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        // Since we're using a pull to refresh, the posts are off by 1
        id--;
        if (mTwoPane) {
            Log.d(TAG, "updating a fragment");
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(PostDetailFragment.ARG_ITEM_ID, id);
            PostDetailFragment fragment = new PostDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.post_detail_container, fragment).commit();

        } else {
            Log.d(TAG, "starting an activity for post at position: " + Integer.toString(id));
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PostDetailActivity.class);
            detailIntent.putExtra(PostDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
