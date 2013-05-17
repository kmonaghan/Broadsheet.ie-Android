package ie.broadsheet.app;

import ie.broadsheet.app.model.json.Post;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BroadsheetApplication extends Application {
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        if (this.posts.size() > 0) {
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
    }
}
