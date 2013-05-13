package ie.broadsheet.app;

import ie.broadsheet.app.services.BroadsheetServices;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.octo.android.robospice.SpiceManager;

public class BaseFragmentActivity extends SherlockFragmentActivity {
    private ProgressDialog mProgressDialog;

    private SpiceManager spiceManager = new SpiceManager(BroadsheetServices.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mProgressDialog.dismiss();
        super.onPause();
    }

    protected void onPreExecute() {
        mProgressDialog.show();
    }

    protected void onPostExecute() {
        mProgressDialog.dismiss();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }
}
