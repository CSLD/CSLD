package cz.larpovadatabaze.services;

import cz.larpovadatabaze.services.impl.ImageResizingStrategyFactoryServiceImpl;
import cz.larpovadatabaze.services.s3.S3Bucket;
import cz.larpovadatabaze.services.s3.S3Files;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.AbstractResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class S3FilesIT {
    private S3Bucket bucket;

    private ImageResizingStrategyFactoryService strategiesToResizeImage;
    private S3Files files;

    @Before
    public void setUp() throws IOException {
        bucket = new S3Bucket("larp-db-integration-test");
        files = new S3Files(bucket);
        strategiesToResizeImage = new ImageResizingStrategyFactoryServiceImpl();
        // Upload file to use further.
        bucket.upload("image/test.jpg",
                new FileInputStream(new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg")));
    }

    @Test
    public void getFullPathToTheResource() {
        String expectedPath = "image/test.jpg";
        String providedPath = files.getPathInDataDir("image/test.jpg");

        assertThat(providedPath, is(expectedPath));
    }

    @Test
    public void getFullPathToPreviewResource() {
        String expectedPath = "image/test-p.jpg";
        String providedPath = files.getFilePreviewInDataDir("image/test.jpg");

        assertThat(providedPath, is(expectedPath));
    }

    private FileUpload prepareDummyFileUpload() {
        FileItem fileToUpload = new DiskFileItem("image", "image/jpeg", false,
                "t0Dh33USkSOzdLwc.jpeg", 1, new File("src/test/java/cz/larpovadatabaze/services/")) {
            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg"));
            }
        };
        return new FileUpload(fileToUpload);
    }

    @Test
    public void saveImageFileAndPreview() {
        FileUpload fileUpload = prepareDummyFileUpload();
        FileService.ResizeAndSaveReturn savedImage = files.saveImageFileAndPreviewAndReturnPath(
                fileUpload,
                strategiesToResizeImage.getMaxWidthHeightStrategy(128, 128),
                strategiesToResizeImage.getMaxWidthHeightStrategy(32, 32));

        String fullImagePath = files.getPathInDataDir(savedImage.path);
        String previewPath = files.getFilePreviewInDataDir(savedImage.path);

        assertThat(bucket.existsObject(fullImagePath), is(true));
        assertThat(bucket.existsObject(previewPath), is(true));

        assertThat(savedImage.savedHeight, is(96));
        assertThat(savedImage.savedWidth, is(128));

        bucket.removeObject(fullImagePath);
        bucket.removeObject(previewPath);
    }

    @Test
    public void saveOnlyImageWithoutPreview() {
        FileUpload fileUpload = prepareDummyFileUpload();
        FileService.ResizeAndSaveReturn savedImage = files.saveImageFileAndReturnPath(
                fileUpload,
                strategiesToResizeImage.getMaxWidthHeightStrategy(128, 128));

        String fullImagePath = files.getPathInDataDir(savedImage.path);
        String previewPath = files.getFilePreviewInDataDir(savedImage.path);

        assertThat(bucket.existsObject(fullImagePath), is(true));
        assertThat(bucket.existsObject(previewPath), is(false));

        assertThat(savedImage.savedHeight, is(96));
        assertThat(savedImage.savedWidth, is(128));

        bucket.removeObject(fullImagePath);
    }

    @Test
    public void respondWithValidFiles() {
        AbstractResource.ResourceResponse respond = files.respondWithFile(
                "image/test.jpg",
                "image/jpeg");

        assertThat(respond.getContentType(), is("image/jpeg"));
        assertThat(respond.getWriteCallback(), is(notNullValue()));
    }

    @Test
    public void removeFiles() throws IOException {
        String key = "/to-remove-t0Dh33USkSOzdLwc.jpeg";
        String keyPreview = "/to-remove-t0Dh33USkSOzdLwc-p.jpeg";
        File toUpload = new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg");
        bucket.upload(key,
                new FileInputStream(toUpload));
        bucket.upload(keyPreview,
                new FileInputStream(toUpload));

        assertThat(bucket.existsObject(key), is(true));
        assertThat(bucket.existsObject(keyPreview), is(true));
        files.removeFiles(key);
        assertThat(bucket.existsObject(key), is(false));
        assertThat(bucket.existsObject(keyPreview), is(false));
    }

    @After
    public void tearDown() throws IOException {
        bucket.delete();
    }
}
