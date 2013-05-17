package ie.broadsheet.app.adapters;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.PostList;
import ie.broadsheet.app.requests.PostListRequest;
import android.content.Context;
import android.util.Log;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class PostListEndlessAdapter extends EndlessAdapter {
    private static final String TAG = "PostListEndlessAdapter";

    boolean hasMore = true;

    int currentPage = 0;

    private PostListRequest postListRequest;

    public PostListEndlessAdapter(Context context) {
        super(context, new PostListAdapter(context), R.layout.post_list_load_more);

        setRunInBackground(false);

        fetchPosts();
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        if (hasMore) {
            currentPage++;
            fetchPosts();
        }

        return hasMore;
    }

    @Override
    protected void appendCachedData() {

    }

    private void fetchPosts() {
        if (postListRequest == null) {
            postListRequest = new PostListRequest();

            postListRequest.setPage(currentPage);

            BaseFragmentActivity activity = (BaseFragmentActivity) getContext();

            activity.getSpiceManager().execute(postListRequest, postListRequest.generateUrl(),
                    DurationInMillis.ONE_MINUTE, new PostListListener());
        }
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class PostListListener implements RequestListener<PostList> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "Failed to get results");
        }

        @Override
        public void onRequestSuccess(final PostList result) {
            Log.d(TAG, "we got results");

            hasMore = (result.getCount_total() > result.getCount());

            ((PostListAdapter) getWrappedAdapter()).addAll(result.getPosts());
            onDataReady();

            postListRequest = null;

            BroadsheetApplication app = (BroadsheetApplication) PostListEndlessAdapter.this.getContext()
                    .getApplicationContext();
            app.setPosts(result.getPosts());
        }
    }
}
