package ie.broadsheet.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;

public class WebViewDialog extends DialogFragment {
    public static WebViewDialog newInstance(String localHtml) {
        WebViewDialog frag = new WebViewDialog();
        Bundle args = new Bundle();
        args.putString("localHtml", localHtml);
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String localHtml = getArguments().getString("localHtml");
        if (localHtml == null) {
            localHtml = "about";
        }

        WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/" + localHtml + ".html");
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(webView);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return dialog.create();
    }
}
