package cz.larpovadatabaze.common.models;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadedFile extends AbstractUploadedFile {
    private FileUpload upload;

    public UploadedFile(FileUpload upload) {
        this.upload = upload;
    }

    public void writeTo(File to) throws Exception {
        upload.writeTo(to);
    }

    public InputStream getInputStream() throws IOException {
        return upload.getInputStream();
    }

    @Override
    protected String getClientFileName() {
        return upload.getClientFileName();
    }
}
