package ie.broadsheet.app.fragments;

import ie.broadsheet.app.R;
import ie.broadsheet.app.adapters.PostListEndlessAdapter;
import ie.broadsheet.app.adapters.PostListEndlessAdapter.PostListLoadedListener;
import ie.broadsheet.app.dialog.AboutDialog;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * A list fragment representing a list of Posts. This fragment also supports tablet devices by allowing list items to be
 * given an 'activated' state upon selection. This helps indicate which item is currently being viewed in a
 * {@link PostDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class PostListFragment extends SherlockListFragment implements OnQueryTextListener, PostListLoadedListener,
        OnClickListener, OnCloseListener {
    private static final String TAG = "PostListFragment";

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position. Only used on
     * tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private Callbacks mCallbacks;

    private PostListEndlessAdapter postListAdapter;

    private PullToRefreshListView mPullRefreshListView;

    private SearchView searchView;

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
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PostListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postListAdapter = new PostListEndlessAdapter(this.getActivity());
        postListAdapter.setPostListLoadedListener(this);
        setListAdapter(postListAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        // Get original ListView and Frame
        ListView originalLv = (ListView) layout.findViewById(android.R.id.list);
        ViewGroup frame = (ViewGroup) originalLv.getParent();

        // Remove old ListView
        frame.removeView(originalLv);

        // Create new PullToRefreshListView and add to Frame
        mPullRefreshListView = new PullToRefreshListView(getActivity());
        frame.addView(mPullRefreshListView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                postListAdapter.reset();
                postListAdapter.fetchPosts();
            }
        });

        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll,
                pauseOnFling);
        mPullRefreshListView.setOnScrollListener(listener);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        getListView().setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_INSET);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!postListAdapter.isLoaded()) {
            postListAdapter.fetchPosts();
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

        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main, menu);

        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_view_about) {
            AboutDialog dialog = new AboutDialog();
            dialog.show(getActivity().getSupportFragmentManager(), "AboutDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, "Query to search for: " + query);
        postListAdapter.reset();
        postListAdapter.setSearchTerm(query);
        postListAdapter.fetchPosts();

        searchView.clearFocus();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "Current search term: " + newText);
        return false;
    }

    @Override
    public boolean onClose() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onPostListLoaded() {
        mPullRefreshListView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {

    }

}
