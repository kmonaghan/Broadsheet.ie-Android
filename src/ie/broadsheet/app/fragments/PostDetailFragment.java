package ie.broadsheet.app.fragments;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.PostDetailActivity;
import ie.broadsheet.app.PostListActivity;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Post;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A fragment representing a single Post detail screen. This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link PostDetailActivity} on handsets.
 */
public class PostDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Post post;

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

            post = app.getPosts().get(getArguments().getInt(ARG_ITEM_ID));
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
