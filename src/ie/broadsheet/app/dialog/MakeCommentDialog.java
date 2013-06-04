package ie.broadsheet.app.dialog;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.Comment;
import ie.broadsheet.app.requests.MakeCommentRequest;
import ie.broadsheet.app.services.BroadsheetServices;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MakeCommentDialog extends SherlockDialogFragment implements OnClickListener {

    public interface CommentMadeListener {
        public void onCommentMade(Comment comment);
    }

    private CommentMadeListener mListener;

    private ProgressDialog mProgressDialog;

    private static final String TAG = "MakeCommentDialog";

    private SpiceManager spiceManager = new SpiceManager(BroadsheetServices.class);

    private int postId;

    private int commentId = 0;

    private EditText email;

    private EditText commenterName;

    private EditText commenterUrl;

    private EditText commentBody;

    private Dialog dialog;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setCommentMadeListener(CommentMadeListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.posting_comment));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_make_comment, null);

        commenterName = (EditText) view.findViewById(R.id.commenterName);
        email = (EditText) view.findViewById(R.id.commenterEmail);
        commenterUrl = (EditText) view.findViewById(R.id.commenterUrl);
        commentBody = (EditText) view.findViewById(R.id.commentBody);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        email.setText(sharedPref.getString("email", ""));
        commenterName.setText(sharedPref.getString("commenterName", ""));
        commenterUrl.setText(sharedPref.getString("commenterUrl", ""));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
        // Add action buttons
                .setPositiveButton(R.string.comment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MakeCommentDialog.this.getDialog().cancel();
                    }
                });

        dialog = builder.create();

        commenterName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        spiceManager.start(getActivity());
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(this);
        }

        ((BroadsheetApplication) getActivity().getApplication()).getTracker().sendView("Post Comment");
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (!validate()) {
            return;
        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("commenterName", commenterName.getText().toString());
        editor.putString("commenterUrl", commenterUrl.getText().toString());
        editor.commit();

        MakeCommentRequest makeCommentRequest = new MakeCommentRequest();
        makeCommentRequest.setPostId(postId);
        makeCommentRequest.setEmail(email.getText().toString());
        makeCommentRequest.setCommentUrl(commenterUrl.getText().toString());
        makeCommentRequest.setCommentName(commenterName.getText().toString());
        makeCommentRequest.setCommentBody(commentBody.getText().toString());
        makeCommentRequest.setCommentId(commentId);

        v.setEnabled(false);

        email.clearFocus();
        commenterName.clearFocus();
        commenterUrl.clearFocus();
        commentBody.clearFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentBody.getWindowToken(), 0);

        mProgressDialog.show();

        spiceManager.execute(makeCommentRequest, new MakeCommentListener());
    }

    private boolean validate() {
        boolean okay = true;

        if ((commenterName.getText().toString() == null) || (commenterName.getText().toString().length() < 1)) {
            okay = false;
            commenterName.setError(getResources().getString(R.string.error_no_name));
        } else {
            commenterName.setError(null);
        }

        if ((email.getText().toString() == null) || (email.getText().toString().length() < 1)) {
            okay = false;
            email.setError(getResources().getString(R.string.error_no_email));
        } else {
            email.setError(null);
        }

        if ((commentBody.getText().toString() == null) || (commentBody.getText().toString().length() < 1)) {
            okay = false;
            commentBody.setError(getResources().getString(R.string.error_no_message));
        } else {
            commentBody.setError(null);
        }

        return okay;
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class MakeCommentListener implements RequestListener<Comment> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "Failed to get results: " + spiceException.toString());

            MakeCommentDialog.this.mProgressDialog.dismiss();

            ((AlertDialog) MakeCommentDialog.this.dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }

        @Override
        public void onRequestSuccess(final Comment result) {
            Log.d(TAG, "we got result: " + result.toString());

            ((AlertDialog) MakeCommentDialog.this.dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

            MakeCommentDialog.this.mProgressDialog.dismiss();

            MakeCommentDialog.this.dismiss();

            MakeCommentDialog.this.mListener.onCommentMade(result);
        }
    }
}
