package ie.broadsheet.app.requests;

import ie.broadsheet.app.BroadsheetApplication;
import ie.broadsheet.app.R;
import ie.broadsheet.app.client.http.MultipartFormDataContent;
import ie.broadsheet.app.client.http.MultipartFormDataContent.Part;
import ie.broadsheet.app.model.json.SubmitTipResponse;

import java.io.File;
import java.io.IOException;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class SubmitTipRequest extends GoogleHttpClientSpiceRequest<SubmitTipResponse> {
    private static final String TAG = "SubmitTipRequest";

    private String name;

    private String email;

    private String message;

    private String filename;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SubmitTipRequest() {
        super(SubmitTipResponse.class);
    }

    @Override
    public SubmitTipResponse loadDataFromNetwork() throws Exception {
        MultipartFormDataContent content = new MultipartFormDataContent();

        if (filename != null) {
            Log.d(TAG, "we have a file: " + filename);

            File file = new File(filename);

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String mimeType = mime.getMimeTypeFromExtension(extension);

            Part part = new Part();
            part.setName("file");
            part.setFilename(file.getName());
            part.setContent(new FileContent(mimeType, file));
            content.addPart(part);

        }

        content.addUrlEncodedContent("name", name);
        content.addUrlEncodedContent("email", email);
        content.addUrlEncodedContent("message", message);
        content.addUrlEncodedContent("android", "android");

        Log.d(TAG, BroadsheetApplication.context().getString(R.string.apiURL) + "/iphone_tip.php");
        HttpRequest request = null;
        try {
            request = getHttpRequestFactory().buildPostRequest(
                    new GenericUrl(BroadsheetApplication.context().getString(R.string.apiURL) + "/iphone_tip.php"),
                    content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }
}
