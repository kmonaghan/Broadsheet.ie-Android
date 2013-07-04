package ie.broadsheet.app.requests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.net.Uri;

import com.octo.android.robospice.request.SpiceRequest;

public class DownloadFileRequest extends SpiceRequest<File> {
    private static final int MAX_BUFFER_SIZE = 1048576;

    private String source;

    private File destinationFolder;

    public DownloadFileRequest(String source, File destinationFolder) {
        super(File.class);
        this.source = source;
        this.destinationFolder = destinationFolder;
    }

    @Override
    public File loadDataFromNetwork() throws IOException {
        int bytesRead;
        URL url = new URL(source);

        File destFile = new File(destinationFolder, Uri.parse(source).getLastPathSegment());
        FileOutputStream fileOutput = new FileOutputStream(destFile);

        InputStream inputStream = (InputStream) url.getContent();
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, bytesRead);
        }
        fileOutput.close();

        return destFile;
    }

    @Override
    public String toString() {
        return "DownloadFileRequest [source=" + source + ", destinationFolder=" + destinationFolder + "]";
    }
}
