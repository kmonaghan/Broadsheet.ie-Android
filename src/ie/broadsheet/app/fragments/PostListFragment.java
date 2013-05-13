package ie.broadsheet.app.fragments;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.adapters.PostListAdapter;
import ie.broadsheet.app.model.json.PostList;
import ie.broadsheet.app.requests.PostListRequest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * A list fragment representing a list of Posts. This fragment also supports tablet devices by allowing list items to be
 * given an 'activated' state upon selection. This helps indicate which item is currently being viewed in a
 * {@link PostDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class PostListFragment extends SherlockListFragment {
    private static final String TAG = "PostListFragment";

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position. Only used on
     * tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private PostListRequest postListRequest;

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows
     * activities to be notified of item selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does nothing. Used only when this fragment is not
     * attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
            Log.d(TAG, "dummy message");
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resuming...so do we fetch posts?");

        if (postListRequest == null) {
            Log.d(TAG, "yes, yes we do");

            postListRequest = new PostListRequest();

            BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

            activity.getSpiceManager().execute(postListRequest, "Sdasdaswewegsdfasdfafdasdasd",
                    DurationInMillis.ONE_MINUTE, new PostListListener());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Log.d(TAG, "item clicked");
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when
     * touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class PostListListener implements RequestListener<PostList> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // onPostExecute();

            Log.d(TAG, "Failed to get results");
        }

        @Override
        public void onRequestSuccess(final PostList result) {
            // onPostExecute();
            Log.d(TAG, "we got results");
            PostListAdapter postListAdapter = new PostListAdapter(PostListFragment.this.getActivity(),
                    result.getPosts(), result.getCount_total());

            PostListFragment.this.setListAdapter(postListAdapter);

            BroadsheetApplication app = (BroadsheetApplication) PostListFragment.this.getActivity().getApplication();
            app.setPosts(result.getPosts());
        }
    }
}
