/**
 * via http://stackoverflow.com/a/16000382/806442
 */
/*
 * Copyright (c) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 * This is a modification of com.google.api.client.http.MultipartContent from Google HTTP Client library to support
 * multipart/form-data requests.
 * 
 * The original author is Yaniv Inbar.
 */
package ie.broadsheet.app.client.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpEncoding;
import com.google.api.client.http.HttpEncodingStreamingContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StreamingContent;

public class MultipartFormDataContent extends AbstractHttpContent {

    private static final String NEWLINE = "\r\n";

    private static final String TWO_DASHES = "--";

    private ArrayList<Part> parts = new ArrayList<Part>();

    public MultipartFormDataContent() {
        super(new HttpMediaType("multipart/form-data").setParameter("boundary", "__END_OF_PART__"));
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {

        Writer writer = new OutputStreamWriter(out, getCharset());
        String boundary = getBoundary();

        for (Part part : parts) {
            HttpHeaders headers = new HttpHeaders().setAcceptEncoding(null);
            if (part.headers != null) {
                headers.fromHttpHeaders(part.headers);
            }
            headers.setContentEncoding(null).setUserAgent(null).setContentType(null).setContentLength(null);
            // analyze the content
            HttpContent content = part.content;
            StreamingContent streamingContent = null;
            String contentDisposition = String.format("form-data; name=\"%s\"", part.name);
            if (part.filename != null) {
                headers.setContentType(content.getType());
                contentDisposition += String.format("; filename=\"%s\"", part.filename);
            }
            headers.set("Content-Disposition", contentDisposition);
            HttpEncoding encoding = part.encoding;
            if (encoding == null) {
                streamingContent = content;
            } else {
                headers.setContentEncoding(encoding.getName());
                streamingContent = new HttpEncodingStreamingContent(content, encoding);
            }
            // write separator
            writer.write(TWO_DASHES);
            writer.write(boundary);
            writer.write(NEWLINE);
            // write headers
            HttpHeaders.serializeHeadersForMultipartRequests(headers, null, null, writer);
            // write content
            if (streamingContent != null) {
                writer.write(NEWLINE);
                writer.flush();
                streamingContent.writeTo(out);
                writer.write(NEWLINE);
            }
        }
        // write end separator
        writer.write(TWO_DASHES);
        writer.write(boundary);
        writer.write(TWO_DASHES);
        writer.write(NEWLINE);
        writer.flush();
    }

    @Override
    public boolean retrySupported() {
        for (Part part : parts) {
            if (!part.content.retrySupported()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MultipartFormDataContent setMediaType(HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    /**
     * Adds an HTTP multipart part.
     * 
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing the return type,
     * but nothing else.
     * </p>
     */
    public MultipartFormDataContent addPart(Part part) {
        parts.add(Preconditions.checkNotNull(part));
        return this;
    }

    public MultipartFormDataContent addUrlEncodedContent(String name, String value) {
        GenericData data = new GenericData();
        data.put(value, "");

        Part part = new Part();
        part.setContent(new UrlEncodedContent(data));
        part.setName(name);

        this.addPart(part);

        return this;
    }

    /** Returns the boundary string to use. */
    public final String getBoundary() {
        return getMediaType().getParameter("boundary");
    }

    /**
     * Sets the boundary string to use.
     * 
     * <p>
     * Defaults to {@code "END_OF_PART"}.
     * </p>
     * 
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing the return type,
     * but nothing else.
     * </p>
     */
    public MultipartFormDataContent setBoundary(String boundary) {
        getMediaType().setParameter("boundary", Preconditions.checkNotNull(boundary));
        return this;
    }

    /**
     * Single part of a multi-part request.
     * 
     * <p>
     * Implementation is not thread-safe.
     * </p>
     */
    public static final class Part {
        private String name;

        private String filename;

        private HttpContent content;

        private HttpHeaders headers;

        private HttpEncoding encoding;

        public Part setContent(HttpContent content) {
            this.content = content;
            return this;
        }

        public Part setHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Part setEncoding(HttpEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        public Part setName(String name) {
            this.name = name;
            return this;
        }

        public Part setFilename(String filename) {
            this.filename = filename;
            return this;
        }
    }

}
