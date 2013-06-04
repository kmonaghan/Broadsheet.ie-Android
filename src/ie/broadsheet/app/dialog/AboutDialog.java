package ie.broadsheet.app.dialog;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class AboutDialog extends DialogFragment implements OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.about_title).setItems(R.array.about_array, this)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((BroadsheetApplication) getActivity().getApplication()).getTracker().sendView("About");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Context context = ((Dialog) dialog).getContext();

        switch (which) {
        case 0:
            WebViewDialog aboutDialog = WebViewDialog.newInstance("about");
            aboutDialog.show(getActivity().getSupportFragmentManager(), "AboutDialog");
            break;
        case 1:
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Couldn't launch the market", Toast.LENGTH_LONG).show();
            }
            break;
        case 2:
            Uri developeruri = Uri.parse("http://karlmonaghan.com/about");
            Intent goToDeveloper = new Intent(Intent.ACTION_VIEW, developeruri);
            startActivity(goToDeveloper);
            break;
        case 3:
            WebViewDialog webViewDialog = WebViewDialog.newInstance("libraries");
            webViewDialog.show(getActivity().getSupportFragmentManager(), "AboutDialog");
            break;
        case 4:
            /* Create the Intent */
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] { "feedback@crayonsandbrownpaper.com" });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback for Broadsheet.ie");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

            /* Send it off to the Activity-Chooser */
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            break;
        }

    }
}
