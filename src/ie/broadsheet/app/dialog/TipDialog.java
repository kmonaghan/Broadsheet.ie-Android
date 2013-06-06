package ie.broadsheet.app.dialog;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.SubmitTipResponse;
import ie.broadsheet.app.requests.SubmitTipRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class TipDialog extends DialogFragment implements android.view.View.OnClickListener {
    private static final String TAG = "TipDialog";

    private static int RESULT_LOAD_IMAGE = 1;

    private EditText mName;

    private EditText mEmail;

    private EditText mMessage;

    private String mPicturePath;

    private boolean mAskedAboutPicture;

    private Button mSelectPhoto;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mAskedAboutPicture = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_submit_tip, null);

        mName = (EditText) view.findViewById(R.id.submitterName);
        mEmail = (EditText) view.findViewById(R.id.submitorEmail);
        mMessage = (EditText) view.findViewById(R.id.submitBody);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        mEmail.setText(sharedPref.getString("email", ""));
        mName.setText(sharedPref.getString("commenterName", ""));

        mSelectPhoto = (Button) view.findViewById(R.id.addImage);
        mSelectPhoto.setOnClickListener(this);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.submit_tip, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.getDialog().cancel();
                    }
                });

        final Dialog dialog = builder.create();

        mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(this);
        }

        ((BroadsheetApplication) getActivity().getApplication()).getTracker().sendView("Submit Tip");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPicturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d(TAG, "Picture path is : " + mPicturePath);

            ImageView imageView = (ImageView) getDialog().findViewById(R.id.sumbitorImage);
            imageView.setImageBitmap(BitmapFactory.decodeFile(mPicturePath));
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addImage) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } else {
            Log.d(TAG, "clicked submit button");

            if (!validate()) {
                return;
            }

            if (!mAskedAboutPicture && (mPicturePath == null)) {
                askAboutPicture();
                mAskedAboutPicture = true;

                return;
            }

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("email", mEmail.getText().toString());
            editor.putString("commenterName", mName.getText().toString());
            editor.commit();

            SubmitTipRequest request = new SubmitTipRequest();
            request.setName(mName.getText().toString());
            request.setEmail(mEmail.getText().toString());
            request.setMessage(mMessage.getText().toString());
            request.setFilename(mPicturePath);

            mEmail.clearFocus();
            mName.clearFocus();
            mMessage.clearFocus();

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMessage.getWindowToken(), 0);

            ((BaseFragmentActivity) getActivity()).onPreExecute(getResources().getString(R.string.posting_tip));

            BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();

            activity.getSpiceManager().execute(request, new SubmitTipRequestListener());
        }
    }

    public void askAboutPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.no_image_picked)
                .setPositiveButton(R.string.add_an_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.onClick(mSelectPhoto);
                    }
                }).setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.onClick(null);
                    }
                });

        builder.create().show();
    }

    private boolean validate() {
        boolean okay = true;

        if ((mName.getText().toString() == null) || (mName.getText().toString().length() < 1)) {
            okay = false;
            mName.setError(getResources().getString(R.string.error_no_name));
        } else {
            mName.setError(null);
        }

        if ((mEmail.getText().toString() == null) || (mEmail.getText().toString().length() < 1)) {
            okay = false;
            mEmail.setError(getResources().getString(R.string.error_no_email));
        } else {
            mEmail.setError(null);
        }

        if ((mMessage.getText().toString() == null) || (mMessage.getText().toString().length() < 1)) {
            okay = false;
            mMessage.setError(getResources().getString(R.string.error_no_message));
        } else {
            mMessage.setError(null);
        }

        return okay;
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class SubmitTipRequestListener implements RequestListener<SubmitTipResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "Failed to get results: " + spiceException.toString());

            ((BaseFragmentActivity) getActivity()).onPostExecute();

            ((BaseFragmentActivity) getActivity()).showError(getActivity().getResources().getString(
                    R.string.tip_submit_problem));
        }

        @Override
        public void onRequestSuccess(final SubmitTipResponse result) {
            Log.d(TAG, "we got result: " + result.toString());

            ((BaseFragmentActivity) getActivity()).onPostExecute();

            TipDialog.this.dismiss();
        }
    }
}
