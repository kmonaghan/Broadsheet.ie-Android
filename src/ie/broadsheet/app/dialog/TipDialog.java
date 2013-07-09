package ie.broadsheet.app.dialog;

import ie.broadsheet.app.BaseFragmentActivity;
import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.model.json.SubmitTipResponse;
import ie.broadsheet.app.requests.DownloadFileRequest;
import ie.broadsheet.app.requests.SubmitTipRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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

    private static final int IMAGE_REQUEST_CODE = 1000;

    public static final String CURRENT_IMAGE_FILENAME = "image_filename";

    private EditText mName;

    private EditText mEmail;

    private EditText mMessage;

    private String mPicturePath;

    private String mCurrentPhotoPath;

    private boolean mAskedAboutPicture;

    private Button mSelectPhoto;

    private DownloadFileRequest downloadFileRequest;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(CURRENT_IMAGE_FILENAME);
        }

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

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                final boolean isCamera = (data == null)
                        || android.provider.MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());

                if (isCamera) {
                    mPicturePath = mCurrentPhotoPath;

                    galleryAddPic();

                    Log.d(TAG, "From the camera: " + mPicturePath);

                    showImage();
                } else {
                    Uri imageUri = data.getData();
                    // some devices (OS versions return an URI of com.android instead of com.google.android
                    if (imageUri.toString().startsWith("content://com.android.gallery3d.provider")) {
                        imageUri = Uri.parse(imageUri.toString().replace("com.android.gallery3d",
                                "com.google.android.gallery3d"));
                    }

                    mPicturePath = null;
                    String[] projection = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(imageUri, projection, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        if (column_index != -1) {
                            cursor.moveToFirst();
                            mPicturePath = cursor.getString(column_index);
                        }
                    }

                    if (mPicturePath == null) mPicturePath = imageUri.getPath();

                    Log.d(TAG, "from gallery: " + mPicturePath);

                    if (mPicturePath != null) {
                        File file = new File(mPicturePath);
                        if (file.exists()) {
                            showImage();
                        } else if (mPicturePath.matches("http(s)?://.*"))
                            downloadImage();
                        else {
                            // error
                            Log.d(TAG, "we've an error as the file doesn't exist or isn't a URL");
                        }

                    } else {
                        // Error
                        Log.d(TAG, "we've an error as the file doesn't exist or isn't a URL");
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addImage) {
            selectImage();
        } else {
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

    public void showImage() {
        ImageView imageView = (ImageView) getDialog().findViewById(R.id.sumbitorImage);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPicturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mPicturePath, bmOptions);

        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
    }

    protected void selectImage() {
        File tempFile = null;
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mCurrentPhotoPath = tempFile.getAbsolutePath();

        Uri outputFileUri = Uri.fromFile(tempFile);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        final Intent chooserIntent = Intent.createChooser(pickPhoto, getResources().getString(R.string.selectGallery));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
        startActivityForResult(chooserIntent, IMAGE_REQUEST_CODE);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.app_name));

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BS_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", getAlbumDir());
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void downloadImage() {
        if (downloadFileRequest != null && !downloadFileRequest.isCancelled()) downloadFileRequest.cancel();
        downloadFileRequest = new DownloadFileRequest(mPicturePath, getActivity().getExternalCacheDir());
        ((BaseFragmentActivity) getActivity()).getSpiceManager().execute(downloadFileRequest,
                new DownloadRequestListener());
        ((BaseFragmentActivity) getActivity()).onPreExecute(getResources().getString(R.string.downloadingFile));
    }

    public void askAboutPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.no_image_picked)
                .setPositiveButton(R.string.add_an_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.selectImage();
                    }
                }).setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TipDialog.this.onClick((Button) ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE));
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

    private class DownloadRequestListener implements RequestListener<File> {

        @Override
        public void onRequestFailure(SpiceException e) {
            TipDialog.this.downloadFileRequest = null;
            ((BaseFragmentActivity) getActivity()).onPostExecute();
            Log.d(TAG, "Problem downloading");
        }

        @Override
        public void onRequestSuccess(File result) {
            TipDialog.this.downloadFileRequest = null;
            ((BaseFragmentActivity) getActivity()).onPostExecute();
            Log.d(TAG, result.toString());

            TipDialog.this.mPicturePath = result.getPath();
            TipDialog.this.showImage();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "saving instance");

        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(CURRENT_IMAGE_FILENAME, mCurrentPhotoPath);
    }
}
