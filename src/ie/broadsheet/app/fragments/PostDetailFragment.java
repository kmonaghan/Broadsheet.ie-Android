package ie.broadsheet.app.fragments;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.PostDetailActivity;
import ie.broadsheet.app.PostListActivity;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Post;
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

            webview.loadData(postHTML, "text/html", null);
            webview.setWebViewClient(new WebViewClient());
        }

        return rootView;
    }
}
