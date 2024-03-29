package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.common.models.AbstractUploadedFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Base64;

public class GraphQLUploadedFile extends AbstractUploadedFile {
    private final String fileName;
    private final byte[] contents;

    public GraphQLUploadedFile(String fileName, String contents) {
        this.fileName = fileName;
        this.contents = Base64.getDecoder().decode(contents);
    }

    @Override
    public void writeTo(File to) throws Exception {
        FileOutputStream stream = new FileOutputStream(to);
        stream.write(contents);
        stream.close();
    }

    @Override
    protected String getClientFileName() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(contents);
    }
}
