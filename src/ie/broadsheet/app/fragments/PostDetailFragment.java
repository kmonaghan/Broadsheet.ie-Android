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

    private Post post;

    private WebView webview;

    private int postIndex = -1;

    private ShareActionProvider actionProvider;

    private Button next;

    private Button previous;

    private BroadsheetApplication app;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PostDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getArguments().getString(ARG_ITEM_URL);

        if (url != null) {

            ((BaseFragmentActivity) getActivity()).onPreExecute(getResources().getString(R.string.posting_comment));

            BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

            PostRequest postRequest = new PostRequest(url);

            activity.getSpiceManager().execute(postRequest, url, DurationInMillis.ONE_MINUTE, new PostListener());
        } else if (getArguments().containsKey(ARG_ITEM_ID)) {
            app = (BroadsheetApplication) getActivity().getApplication();
            postIndex = getArguments().getInt(ARG_ITEM_ID);
            post = app.getPosts().get(postIndex);
        }

        setHasOptionsMenu(true);

        getActivity().setTitle("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (post != null) {

            inflater.inflate(R.menu.posts, menu);

            menu.findItem(R.id.menu_make_comment).setVisible(post.getComment_status().equals("open"));

            menu.findItem(R.id.menu_view_comments).setVisible((post.getComment_count() > 0));

            MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);

            actionProvider = (ShareActionProvider) actionItem.getActionProvider();
            actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

            actionProvider.setShareIntent(createShareIntent());
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

        webview = (WebView) rootView.findViewById(R.id.webview);

        next = (Button) rootView.findViewById(R.id.next);
        previous = (Button) rootView.findViewById(R.id.previous);

        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        if (postIndex == -1) {
            next.setVisibility(View.GONE);
            previous.setVisibility(View.GONE);
        }

        if (post != null) {
            layoutView();
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_view_comments) {
            Intent commentIntent = new Intent(this.getActivity(), CommentListActivity.class);
            commentIntent.putExtra(PostDetailFragment.ARG_ITEM_ID, postIndex);
            startActivity(commentIntent);
            return true;
        } else if (item.getItemId() == R.id.menu_make_comment) {

            MakeCommentDialog dialog = new MakeCommentDialog();
            dialog.setPostId(post.getId());
            dialog.setCommentMadeListener(this);
            dialog.show(getActivity().getSupportFragmentManager(), "MakeCommentDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        String shareBody = post.getTitle() + " - " + post.getUrl();

        sharingIntent.setType("text/plain").putExtra(android.content.Intent.EXTRA_SUBJECT, "Broadsheet.ie")
                .putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        return sharingIntent;
    }

    @Override
    public void onCommentMade(Comment comment) {
        post.addComment(comment);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void layoutView() {
        String postHTML = "<html><head>";
        postHTML += "<style>#singlentry {font-size: 16px;}</style><link href='default.css' rel='stylesheet' type='text/css' />";
        postHTML += "</head><body id=\"contentbody\"><div id='maincontent' class='content'><div class='post'><div id='title'>"
                + post.getTitle()
                + "</div><div><span class='date-color'>"
                + post.getDate()
                + "</span>&nbsp;<a class='author' href=\"kmwordpress://author:%@\">"
                + post.getAuthor().getName()
                + "</a></div>";
        postHTML += "<div id='singlentry'>" + post.getContent() + "</div></div>";
        postHTML += "</div></body></html>";

        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("file:///android_asset/", postHTML, "text/html", "UTF-8", null);
        webview.setWebViewClient(new MyWebViewClient(this.getActivity()));

        next.setEnabled((postIndex > 0));
        previous.setEnabled(((postIndex + 1) < app.getPosts().size()));

        app.getTracker()
                .sendView("Post " + Html.fromHtml(post.getTitle_plain()) + " " + Integer.toString(post.getId()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next) {
            postIndex--;
        } else if (v.getId() == R.id.previous) {
            postIndex++;
        }

        post = app.getPosts().get(postIndex);

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
            } else if (url.contains("broadsheet.ie/20")) {
                viewBroadsheetPost(mActivity, url);
                return true;
            }

            return false;
        }

        public void viewYoutube(Context context, String url) {
            viewWithPackageName(context, url, "com.google.android.youtube");
        }

        public void viewBroadsheetPost(Context context, String url) {
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
            PostDetailFragment.this.post = result.getPost();

            ((BaseFragmentActivity) getActivity()).onPostExecute();

            PostDetailFragment.this.getActivity().invalidateOptionsMenu();

            PostDetailFragment.this.layoutView();
        }
    }
}
