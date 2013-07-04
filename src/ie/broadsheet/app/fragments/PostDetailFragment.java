package ie.broadsheet.app.fragments;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.CommentListActivity;
import ie.broadsheet.app.PostDetailActivity;
import ie.broadsheet.app.PostListActivity;
import ie.broadsheet.app.R;
import ie.broadsheet.app.dialog.MakeCommentDialog;
import ie.broadsheet.app.model.json.Comment;
import ie.broadsheet.app.model.json.Post;
import ie.broadsheet.app.model.json.SinglePost;
import ie.broadsheet.app.requests.PostRequest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * A fragment representing a single Post detail screen. This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link PostDetailActivity} on handsets.
 */
public class PostDetailFragment extends SherlockFragment implements MakeCommentDialog.CommentMadeListener,
        OnClickListener {
    private static final String TAG = "PostDetailFragment";

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    public static final String ARG_ITEM_URL = "item_url";

    public static final String CURRENT_POST = "current_post";

    public static final String CURRENT_POST_ID = "current_post_id";

    private Post mPost;

    private WebView mWebview;

    private int mPostIndex = -1;

    private ShareActionProvider mActionProvider;

    private Button mNext;

    private Button mPrevious;

    private BroadsheetApplication mApp;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PostDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (BroadsheetApplication) getActivity().getApplication();

        if (savedInstanceState != null) {
            Log.d(TAG, "saved instance");
            mPost = (Post) savedInstanceState.getSerializable(CURRENT_POST);
            mPostIndex = savedInstanceState.getInt(CURRENT_POST_ID, -1);
        } else {
            String url = getArguments().getString(ARG_ITEM_URL);

            if (url != null) {

                ((BaseFragmentActivity) getActivity()).onPreExecute(getResources().getString(R.string.posting_comment));

                BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

                PostRequest postRequest = new PostRequest(url);

                activity.getSpiceManager().execute(postRequest, url, DurationInMillis.ONE_MINUTE, new PostListener());
            } else if (getArguments().containsKey(ARG_ITEM_ID)) {

                mPostIndex = getArguments().getInt(ARG_ITEM_ID);
                if (mPostIndex < mApp.getPosts().size()) {
                    mPost = mApp.getPosts().get(mPostIndex);
                }
            }
        }

        setHasOptionsMenu(true);

        getActivity().setTitle("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mPost != null) {

            inflater.inflate(R.menu.post_detail, menu);

            menu.findItem(R.id.menu_make_comment).setVisible(mPost.getComment_status().equals("open"));

            menu.findItem(R.id.menu_view_comments).setVisible((mPost.getComment_count() > 0));

            MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);

            mActionProvider = (ShareActionProvider) actionItem.getActionProvider();
            mActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

            mActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_detail, container, false);

        mWebview = (WebView) rootView.findViewById(R.id.webview);

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new MyWebViewClient(this.getActivity()));
        if (android.os.Build.VERSION.SDK_INT < 8) {
            mWebview.getSettings().setPluginsEnabled(true);
        } else {
            mWebview.getSettings().setPluginState(PluginState.ON);
        }

        mNext = (Button) rootView.findViewById(R.id.next);
        mPrevious = (Button) rootView.findViewById(R.id.previous);

        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);

        if (mPostIndex == -1) {
            mNext.setVisibility(View.GONE);
            mPrevious.setVisibility(View.GONE);
        }

        if (mPost != null) {
            layoutView();
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_view_comments) {
            Intent commentIntent = new Intent(this.getActivity(), CommentListActivity.class);
            commentIntent.putExtra(PostDetailFragment.ARG_ITEM_ID, mPostIndex);
            startActivity(commentIntent);
            return true;
        } else if (item.getItemId() == R.id.menu_make_comment) {

            MakeCommentDialog dialog = new MakeCommentDialog();
            dialog.setPostId(mPost.getId());
            dialog.setCommentMadeListener(this);
            dialog.show(getActivity().getSupportFragmentManager(), "MakeCommentDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        String shareBody = mPost.getTitle() + " - " + mPost.getUrl();

        sharingIntent.setType("text/plain").putExtra(android.content.Intent.EXTRA_SUBJECT, "Broadsheet.ie")
                .putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        return sharingIntent;
    }

    @Override
    public void onCommentMade(Comment comment) {
        mPost.addComment(comment);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void layoutView() {
        String postHTML = "<html><head>";
        postHTML += "<style>#singlentry {font-size: 16px;}</style><link href='default.css' rel='stylesheet' type='text/css' />";
        postHTML += "</head><body id=\"contentbody\"><div id='maincontent' class='content'><div class='post'><div id='title'>"
                + mPost.getTitle()
                + "</div><div><span class='date-color'>"
                + mPost.getDate()
                + "</span>&nbsp;<a class='author' href=\"kmwordpress://author:%@\">"
                + mPost.getAuthor().getName()
                + "</a></div>";
        postHTML += "<div id='singlentry'>" + mPost.getContent() + "</div></div>";
        postHTML += "</div></body></html>";

        mWebview.loadDataWithBaseURL("file:///android_asset/", postHTML, "text/html", "UTF-8", null);

        if ((mApp != null) && (mApp.getPosts().size() > 0)) {
            mNext.setEnabled(((mPostIndex + 1) < mApp.getPosts().size()));
            mPrevious.setEnabled((mPostIndex > 0));
        }

        getActivity().supportInvalidateOptionsMenu();

        if (mPost != null) {
            mApp.getTracker().sendView(
                    "Post " + Html.fromHtml(mPost.getTitle_plain()) + " " + Integer.toString(mPost.getId()));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next) {
            mPostIndex++;
        } else if (v.getId() == R.id.previous) {
            mPostIndex--;
        }

        if (mPostIndex == -1) {
            return;
        }

        mPost = mApp.getPosts().get(mPostIndex);

        layoutView();
    }

    // Via
    // http://stackoverflow.com/questions/14088623/android-webview-to-play-youtube-videos
    public class MyWebViewClient extends WebViewClient {

        public Activity mActivity;

        public MyWebViewClient(Activity activity) {
            super();
            mActivity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getHost().contains("youtube.com")) {
                viewYoutube(mActivity, url);
                return true;
            }
            if (uri.getHost().contains("vimeo.com")) {
                viewVimeo(mActivity, url);
                return true;
            } else if (url.contains("broadsheet.ie/20")) {
                viewBroadsheetPost(url);
                return true;
            }

            return false;
        }

        public void viewYoutube(Context context, String url) {
            viewWithPackageName(context, url, "com.google.android.youtube");
        }

        public void viewVimeo(Context context, String url) {
            Intent postIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(postIntent);
        }

        public void viewBroadsheetPost(String url) {
            Intent postIntent = new Intent(getActivity(), PostDetailActivity.class);
            postIntent.putExtra(PostDetailFragment.ARG_ITEM_URL, url);
            startActivity(postIntent);
        }

        public void viewWithPackageName(Context context, String url, String packageName) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (isAppInstalled(context, packageName)) {
                    viewIntent.setPackage(packageName);
                }
                context.startActivity(viewIntent);
            } catch (Exception e) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(viewIntent);
            }
        }

        public boolean isAppInstalled(Context context, String packageName) {
            PackageManager packageManager = context.getPackageManager();
            try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (NameNotFoundException e) {
            }
            return false;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            String javascript = "javascript:" + "var iframes = document.getElementsByTagName('iframe');"
                    + "for (var i = 0, l = iframes.length; i < l; i++) {" + "   var iframe = iframes[i],"
                    + "   a = document.createElement('a');" + "   a.setAttribute('href', iframe.src);"
                    + "   d = document.createElement('div');" + "   d.style.width = iframe.offsetWidth + 'px';"
                    + "   d.style.height = iframe.offsetHeight + 'px';" + "   d.style.top = iframe.offsetTop + 'px';"
                    + "   d.style.left = iframe.offsetLeft + 'px';" + "   d.style.position = 'absolute';"
                    + "   d.style.opacity = '0';" + "   d.style.filter = 'alpha(opacity=0)';"
                    + "   d.style.background = 'black';" + "   a.appendChild(d);"
                    + "   iframe.offsetParent.appendChild(a);" + "}";
            view.loadUrl(javascript);

            super.onPageFinished(view, url);
        }
    }

    public final class PostListener implements RequestListener<SinglePost> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "Failed to get post");

            ((BaseFragmentActivity) getActivity()).onPostExecute();

            BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

            activity.showError(getActivity().getString(R.string.post_load_problem));
        }

        @Override
        public void onRequestSuccess(final SinglePost result) {
            Log.d(TAG, "we got a post: " + result.toString());
            PostDetailFragment.this.mPost = result.getPost();

            ((BaseFragmentActivity) getActivity()).onPostExecute();

            PostDetailFragment.this.getActivity().invalidateOptionsMenu();

            PostDetailFragment.this.layoutView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "saving instance");

        super.onSaveInstanceState(savedInstanceState);

        if (mPostIndex != -1) {
            savedInstanceState.putInt(CURRENT_POST_ID, mPostIndex);
        }

        savedInstanceState.putSerializable(CURRENT_POST, mPost);
    }
}
