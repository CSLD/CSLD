package cz.larpovadatabaze.common.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Base for uploaded files used by Wicket & GraphQL
 */
public abstract class AbstractUploadedFile {

    public String filePath() {
        String fileType = candidateFileType();
        // Generate name
        String fileName = RandomStringUtils.randomAlphanumeric(16) + "." + fileType;
        String dirName = fileName.substring(0, 1) + "/" + fileName.substring(1, 2);
        return dirName + '/' + fileName;
    }

    public String candidateFileType() {
        String fileType;
        String ct = getClientFileName();
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
        String fileName = getClientFileName();
        String[] fileParts = fileName.trim().split("\\.");
        if (fileParts.length > 0) {
            return fileParts[fileParts.length - 1];
        } else {
            return "";
        }
    }

    public abstract void writeTo(File to) throws Exception;

    protected abstract String getClientFileName();

    public abstract InputStream getInputStream() throws IOException;

}
