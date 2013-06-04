package ie.broadsheet.app;

import ie.broadsheet.app.model.json.Post;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BroadsheetApplication extends Application {
    private static BroadsheetApplication mApp = null;

    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    private Tracker mGaTracker;

    private GoogleAnalytics mGaInstance;

    public void setPosts(List<Post> posts) {
        if ((this.posts == null) || (posts == null)) {
            this.posts = posts;
        } else if (this.posts.size() > 0) {
            this.posts.addAll(posts);
        } else {
            this.posts = posts;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        this.posts = new ArrayList<Post>();

        Crashlytics.start(this);

        mGaInstance = GoogleAnalytics.getInstance(this);

        mGaTracker = mGaInstance.getTracker("UA-5653857-3");

        mApp = this;
    }

    public Tracker getTracker() {
        return mGaTracker;
    }

    public static Context context() {
        return mApp.getApplicationContext();
    }

}
