package ie.broadsheet.app.fragments;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.CommentListActivity;
import ie.broadsheet.app.PostDetailActivity;
import ie.broadsheet.app.PostListActivity;
import ie.broadsheet.app.R;
import ie.broadsheet.app.dialog.MakeCommentDialog;
import ie.broadsheet.app.model.json.Post;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

/**
 * A fragment representing a single Post detail screen. This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link PostDetailActivity} on handsets.
 */
public class PostDetailFragment extends SherlockFragment implements MakeCommentDialog.NoticeDialogListener {
    private static final String TAG = "PostDetailFragment";

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Post post;

    int postIndex;

    private ShareActionProvider mShareActionProvider;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PostDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            BroadsheetApplication app = (BroadsheetApplication) getActivity().getApplication();
            postIndex = getArguments().getInt(ARG_ITEM_ID);
            post = app.getPosts().get(postIndex);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.posts, menu);
        /*
         * MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar); ShareActionProvider
         * actionProvider = (ShareActionProvider) actionItem.getActionProvider();
         * actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME); // Note that you
         * can set/change the intent any time, // say when the user has selected an image.
         * actionProvider.setShareIntent(createShareIntent());
         */
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleAnalytics mGaInstance = GoogleAnalytics.getInstance(getActivity());

        if (mGaInstance != null) {
            Tracker mGaTracker = mGaInstance.getDefaultTracker();
            if (mGaTracker != null) {
                mGaTracker.sendView(post.getTitle());
                Log.d(TAG, "Logged view");
            } else {
                Log.d(TAG, "no GA Tracker instance");
            }
        } else {
            Log.d(TAG, "no GA instance");
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_detail, container, false);

        if (post != null) {

            WebView webview = (WebView) rootView.findViewById(R.id.webview);

            String postHTML = "<html><head><script type=\"text/javascript\" src=\"kmwordpress.js\"></script>";
            postHTML += "<style>#singlentry {font-size: 16px;}</style><link href='default.css' rel='stylesheet' type='text/css' />";
            postHTML += "</head><body id=\"contentbody\"><div id='maincontent' class='content'><div class='post'><div id='title'>"
                    + post.getTitle()
                    + "</div><div><span class='date-color'>"
                    + post.getDate()
                    + "</span>&nbsp;<a class='author' href=\"kmwordpress://author:%@\">"
                    + post.getAuthor().getNickname() + "</a></div>";
            postHTML += "<div id='singlentry'>" + post.getContent() + "</div></div>";
            postHTML += "</div></body></html>";

            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadDataWithBaseURL("file:///android_asset/", postHTML, "text/html", "UTF-8", null);
            webview.setWebViewClient(new MyWebViewClient(this.getActivity()));
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
            DialogFragment dialog = new MakeCommentDialog();
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
    public void onDialogPositiveClick(SherlockDialogFragment dialog) {
        // User touched the dialog's positive button
    }

    // Via http://stackoverflow.com/questions/14088623/android-webview-to-play-youtube-videos
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

            return false;
        }

        public void viewYoutube(Context context, String url) {
            viewWithPackageName(context, url, "com.google.android.youtube");
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
}
