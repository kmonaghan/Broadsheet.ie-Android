package ie.broadsheet.app;

import ie.broadsheet.app.services.BroadsheetServices;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.octo.android.robospice.SpiceManager;

public class BaseFragmentActivity extends SherlockFragmentActivity {
    private ProgressDialog mProgressDialog;

    private SpiceManager spiceManager = new SpiceManager(BroadsheetServices.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        EasyTracker.getInstance().setContext(this);

    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();

        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();

        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onPause() {
        mProgressDialog.dismiss();
        super.onPause();
    }

    public void onPreExecute(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void onPostExecute() {
        mProgressDialog.dismiss();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

    public void showError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error).setPositiveButton(R.string.okay, null);

        builder.create().show();
    }
}
