package ie.broadsheet.app.dialog;

import ie.broadsheet.app.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class MakeCommentDialog extends SherlockDialogFragment {

    public interface MakeCommentDialogListener {
        public void onDialogPositiveClick(String email, String commenterName, String commentBody);
    }

    private MakeCommentDialogListener mListener;

    private EditText email;

    private EditText commenterName;

    private EditText commentBody;

    public MakeCommentDialogListener getmListener() {
        return mListener;
    }

    public void setmListener(MakeCommentDialogListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_make_comment, null))
        // Add action buttons
                .setPositiveButton(R.string.comment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        email = (EditText) ((Dialog) dialog).findViewById(R.id.commenterEmail);
                        commenterName = (EditText) ((Dialog) dialog).findViewById(R.id.commenterName);
                        commentBody = (EditText) ((Dialog) dialog).findViewById(R.id.commentBody);

                        mListener.onDialogPositiveClick(email.getText().toString(), commenterName.getText().toString(),
                                commentBody.getText().toString());

                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MakeCommentDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

}
