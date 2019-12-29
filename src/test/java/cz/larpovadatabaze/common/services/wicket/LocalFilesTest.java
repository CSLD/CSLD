package cz.larpovadatabaze.common.services.wicket;

import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
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

public class LocalFilesTest {
    private LocalFiles files;
    private ImageResizingStrategyFactoryService strategiesToResizeImage;
    private String pathToUploadDirectory = "./tmpWicketTest";

    @Before
    public void setUp() throws IOException {
        if(!new File(pathToUploadDirectory).mkdirs()) {
            throw new RuntimeException("It wasn't possible to create the temporary directory.");
        }
        files = new LocalFiles(pathToUploadDirectory);
        strategiesToResizeImage = new ImageResizingStrategyFactoryServiceImpl();
        FileUtils.copyFile(
                new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg"),
                new File(pathToUploadDirectory + "/t0Dh33USkSOzdLwc.jpeg")
        );
    }

    @Test
    public void getFullPathToTheResource() {
        String expectedPath = new File("./tmpWicketTest/image/test.jpg").getAbsolutePath();
        String providedPath = files.getPathInDataDir("image/test.jpg");

        assertThat(providedPath, is(expectedPath));
    }

    @Test
    public void getFullPathToPreviewResource() {
        String expectedPath = new File("./tmpWicketTest/image/test-p.jpg").getAbsolutePath();
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
    public void saveImageFileAndPreview() throws IOException {
        FileUpload fileUpload = prepareDummyFileUpload();
        FileService.ResizeAndSaveReturn savedImage = files.saveImageFileAndPreviewAndReturnPath(
                fileUpload,
                strategiesToResizeImage.getMaxWidthHeightStrategy(128, 128),
                strategiesToResizeImage.getMaxWidthHeightStrategy(32, 32));

        File savedImageFull = new File(files.getPathInDataDir(savedImage.path));
        File savedImagePreview = new File(files.getFilePreviewInDataDir(savedImage.path));
        assertThat(savedImageFull.exists(), is(true));
        assertThat(savedImagePreview.exists(), is(true));

        assertThat(savedImage.savedHeight, is(96));
        assertThat(savedImage.savedWidth, is(128));

        if(!savedImageFull.delete() | !savedImagePreview.delete()){
            throw new IOException("It wasn't possible to delete file");
        }
    }

    @Test
    public void saveOnlyImageWithoutPreview() throws IOException {
        FileUpload fileUpload = prepareDummyFileUpload();
        FileService.ResizeAndSaveReturn savedImage = files.saveImageFileAndReturnPath(
                fileUpload,
                strategiesToResizeImage.getMaxWidthHeightStrategy(128, 128));

        File savedImageFull = new File(files.getPathInDataDir(savedImage.path));
        File savedImagePreview = new File(files.getFilePreviewInDataDir(savedImage.path));
        assertThat(savedImageFull.exists(), is(true));
        assertThat(savedImagePreview.exists(), is(false));

        if(!savedImageFull.delete()){
            throw new IOException("It wasn't possible to delete file");
        }
    }

    @Test
    public void respondWithValidFiles() {
        AbstractResource.ResourceResponse respond = files.respondWithFile(
                new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg").getAbsolutePath(),
                "image/jpeg");

        assertThat(respond.getContentType(), is("image/jpeg"));
        assertThat(respond.getWriteCallback(), is(notNullValue()));
    }

    @Test
    public void removeFiles() throws IOException {
        String pathToTheFile = "/to-remove-t0Dh33USkSOzdLwc.jpeg";
        File toCreateAndRemove = new File(pathToUploadDirectory, pathToTheFile);
        FileUtils.copyFile(
                new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg"),
                toCreateAndRemove
        );

        assertThat(toCreateAndRemove.exists(), is(true));
        files.removeFiles(pathToTheFile);
        assertThat(toCreateAndRemove.exists(), is(false));
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(pathToUploadDirectory));
    }
}
