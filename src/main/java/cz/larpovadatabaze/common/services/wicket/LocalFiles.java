package cz.larpovadatabaze.common.services.wicket;

import cz.larpovadatabaze.common.models.AbstractUploadedFile;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService.IImageResizingStrategy;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.file.Files;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;

/**
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 20:44
 */
public class LocalFiles implements FileService {
    private final static Logger logger = LogManager.getLogger();;

    /**
     * Base data directory
     */
    private String dataDir;

    public LocalFiles(String dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Callback to write a file
     */
    private static class FileWriteCallback extends AbstractResource.WriteCallback {

        private final File file;

        /**
         * @param file File to write
         */
        private FileWriteCallback(File file) {
            this.file = file;
        }

        @Override
        public void writeData(IResource.Attributes attributes) throws IOException {
            byte buf[] = new byte[65536];

            InputStream is = null;

            try {
                is = new FileInputStream(file);
                for (; ; ) {
                    int l = is.read(buf, 0, buf.length);
                    if (l <= 0) break; // EOF

                    OutputStream os = attributes.getResponse().getOutputStream();
                    os.write(buf, 0, l);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * Resource implementing file resource
     */
    private static class FileResource extends AbstractResource {
        private final File file;
        private final String contentType;

        private FileResource(File file, String contentType) throws FileNotFoundException {
            if (!file.exists()) throw new FileNotFoundException();
            this.file = file;
            this.contentType = contentType;
        }

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {
            return respondWithFileStatic(file, contentType);
        }
    }

    /**
     * It cleans space of the file given as parameter, if anything with the same name already existed.
     *
     * @param newFile File to clean before further usage.
     */
    private void cleanFileIfExists(File newFile) {
        if (newFile.exists()) {
            // Try to delete the file
            if (!Files.remove(newFile)) {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    @Override
    public String getPathInDataDir(String relativeName) {
        return new File(dataDir, relativeName).getAbsolutePath();
    }

    @Override
    public String getFilePreviewInDataDir(String relativeName) {
        String previewName;
        int di = relativeName.lastIndexOf('.');
        if (di > 0) {
            previewName = relativeName.substring(0, di) + PREVIEW_POSTFIX + relativeName.substring(di);
        } else {
            previewName = relativeName + PREVIEW_POSTFIX;
        }

        return getPathInDataDir(previewName);
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(AbstractUploadedFile upload, IImageResizingStrategy fullImageResizingStrategy, IImageResizingStrategy previewResizingStrategy) {
        // Determine file type
        String fileType;
        String ct = upload.candidateFileType();
        if (StringUtils.isNotBlank(ct)) {
            int si = ct.lastIndexOf('/');
            if (si > 0) fileType = ct.substring(si + 1);
            else fileType = ct;
        } else {
            fileType = upload.fileType();
        }

        // Generate name
        String fileName = RandomStringUtils.randomAlphanumeric(16) + "." + fileType;
        String dirName = fileName.substring(0, 1) + "/" + fileName.substring(1, 2);
        fileName = dirName + '/' + fileName;

        BufferedImage imageGameSized = null;

        // Create directories
        new File(getPathInDataDir(dirName)).mkdirs();

        // Create a new file
        try {

            File newFile = new File(getPathInDataDir(fileName));
            BufferedImage sourceImage = ImageIO.read(upload.getInputStream());
            imageGameSized = fullImageResizingStrategy.convertImage(sourceImage);

            // Check new file, delete if it already existed
            cleanFileIfExists(newFile);

            // Save to new file
            if (!newFile.createNewFile()) {
                throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
            }
            ImageIO.write(imageGameSized, fileType, newFile);

            if (previewResizingStrategy != null) {
                // Create preview
                BufferedImage previewImage = previewResizingStrategy.convertImage(sourceImage);

                File previewFile = new File(getFilePreviewInDataDir(fileName));
                if (!previewFile.createNewFile()) {
                    throw new RuntimeException("Unable to write file " + previewFile.getAbsolutePath());
                }
                ImageIO.write(previewImage, fileType, previewFile);
            }

        } catch (IOException e) {
            logger.error(e);
        }

        int width = 0, height = 0;
        if (imageGameSized != null) {
            width = imageGameSized.getWidth();
            height = imageGameSized.getHeight();
        }
        return new ResizeAndSaveReturn(fileName, width, height);
    }

    @Override
    public String saveFileAndReturnPath(UploadedFile blueprintFile) {
        String filePath = blueprintFile.filePath();

        try {
            blueprintFile.writeTo(new File(getPathInDataDir(filePath)));
            return filePath;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndReturnPath(AbstractUploadedFile upload, IImageResizingStrategy fullImageResizingStrategy) {
        return saveImageFileAndPreviewAndReturnPath(upload, fullImageResizingStrategy, null);
    }

    @Override
    public InputStream getFileAsStream(String relativeName) throws FileNotFoundException {
        return new FileInputStream(getPathInDataDir(relativeName));
    }

    @Override
    public AbstractResource getFileResource(final String relativeName, final String contentType) throws FileNotFoundException {
        return new FileResource(new File(getPathInDataDir(relativeName)), contentType);
    }

    public static AbstractResource.ResourceResponse respondWithFileStatic(final File file, final String contentType) {
        AbstractResource.ResourceResponse res = new AbstractResource.ResourceResponse();

        if (!file.exists()) {
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            return res;
        }

        res.setLastModified(Instant.ofEpochMilli(file.lastModified()));
        res.setContentType(contentType);
        res.setWriteCallback(new FileWriteCallback(file));

        return res;
    }

    @Override
    public AbstractResource.ResourceResponse respondWithFile(final String fullPath, final String contentType) {
        return respondWithFileStatic(new File(fullPath), contentType);
    }

    @Override
    public void removeFiles(String relativePath) {
        if (relativePath != null) {
            File f = new File(getPathInDataDir(relativePath));
            if (f.exists()) {
                if (!f.delete()) {
                    logger.warn("It wasn't possible to delete file " + relativePath);
                }
            }

            f = new File(getFilePreviewInDataDir(relativePath));
            if (f.exists()) {
                if (!f.delete()) {
                    logger.warn("It wasn't possible to delete file " + relativePath);
                }
            }
        }
    }
}
