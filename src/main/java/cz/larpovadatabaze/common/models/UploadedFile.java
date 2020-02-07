package cz.larpovadatabaze.common.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadedFile {
    private FileUpload upload;

    public UploadedFile(FileUpload upload) {
        this.upload = upload;
    }

    public String filePath() {
        String fileType = candidateFileType();
        // Generate name
        String fileName = RandomStringUtils.randomAlphanumeric(16) + "." + fileType;
        String dirName = fileName.substring(0, 1) + "/" + fileName.substring(1, 2);
        return dirName + '/' + fileName;
    }

    public String candidateFileType() {
        String fileType;
        String ct = upload.getClientFileName();
        if (StringUtils.isNotBlank(ct)) {
            int si = ct.lastIndexOf(".");
            if (si > 0) fileType = ct.substring(si + 1);
            else fileType = ct;
        } else {
            fileType = fileType();
        }
        return fileType;
    }

    public String fileType() {
        String fileName = upload.getClientFileName();
        String[] fileParts = fileName.trim().split("\\.");
        if (fileParts.length > 0) {
            return fileParts[fileParts.length - 1];
        } else {
            return "";
        }
    }

    public void writeTo(File to) throws Exception {
        upload.writeTo(to);
    }

    public InputStream getInputStream() throws IOException {
        return upload.getInputStream();
    }
}
