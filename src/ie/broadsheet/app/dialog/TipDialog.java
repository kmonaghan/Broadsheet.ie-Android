package ie.broadsheet.app.dialog;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.SubmitTipResponse;
import ie.broadsheet.app.requests.SubmitTipRequest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class TipDialog extends DialogFragment implements OnClickListener {
    private static final String TAG = "TipDialog";

    private EditText name;

    private EditText email;

    private EditText message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_submit_tip, null);

        name = (EditText) view.findViewById(R.id.submitterName);
        email = (EditText) view.findViewById(R.id.submitorEmail);
        message = (EditText) view.findViewById(R.id.submitBody);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.submit_tip, this)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SubmitTipRequest request = new SubmitTipRequest();
        request.setName(name.getText().toString());
        request.setEmail(email.getText().toString());
        request.setMessage(message.getText().toString());

        BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

        activity.getSpiceManager().execute(request, "", DurationInMillis.NEVER, new SubmitTipRequestListener());
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class SubmitTipRequestListener implements RequestListener<SubmitTipResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "Failed to get results: " + spiceException.toString());
        }

        @Override
        public void onRequestSuccess(final SubmitTipResponse result) {
            Log.d(TAG, "we got result: " + result.toString());

        }
    }
}
