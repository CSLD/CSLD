package cz.larpovadatabaze.common.services.s3;

import cz.larpovadatabaze.common.models.AbstractUploadedFile;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class S3Files implements FileService {
    private final static Logger logger = LogManager.getLogger();;
    private S3Bucket bucket;

    public S3Files(S3Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Callback to write a file
     */
    private static class FileWriteCallback extends AbstractResource.WriteCallback {
        private final String key;
        private final S3Bucket bucket;

        public FileWriteCallback(S3Bucket bucket, String key) {
            this.key = key;
            this.bucket = bucket;
        }

        @Override
        public void writeData(IResource.Attributes attributes) throws IOException {
            byte[] buf = new byte[65536];

            try (InputStream is = bucket.download(key)) {
                for (; ; ) {
                    int l = is.read(buf, 0, buf.length);
                    if (l <= 0) break; // EOF

                    OutputStream os = attributes.getResponse().getOutputStream();
                    os.write(buf, 0, l);
                }
            }
        }
    }

    /**
     * Resource implementing file from S3 resource
     */
    private static class FileResource extends AbstractResource {
        private final S3Bucket bucket;
        private final String key;
        private final String contentType;

        public FileResource(S3Bucket bucket, String key, String contentType) {
            this.bucket = bucket;
            this.key = key;
            this.contentType = contentType;
        }

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {
            return respondWithFileFromS3(bucket, key, contentType);
        }
    }

    public static AbstractResource.ResourceResponse respondWithFileFromS3(final S3Bucket bucket, final String key, final String contentType) {
        logger.log(Level.INFO, "Bucket: " + bucket.toString() + " Key: " + key);
        AbstractResource.ResourceResponse res = new AbstractResource.ResourceResponse();

        if (!bucket.existsObject(key)) {
            logger.error("Key: " + key + " Doesn't exist in the bucket: " + bucket.toString());
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            return res;
        }


        res.setLastModified(bucket.getLastUpdatedForObject(key));
        res.setContentType(contentType);
        res.setWriteCallback(new S3Files.FileWriteCallback(bucket, key));

        return res;
    }

    @Override
    public String getPathInDataDir(String key) {
        return key;
    }

    @Override
    public String getFilePreviewInDataDir(String key) {
        String previewName;
        int di = key.lastIndexOf('.');
        if (di > 0) {
            previewName = key.substring(0, di) + PREVIEW_POSTFIX + key.substring(di);
        } else {
            previewName = key + PREVIEW_POSTFIX;
        }

        return previewName;
    }

    @Override
    public AbstractResource getFileResource(String relativeName, String contentType) throws FileNotFoundException {
        logger.debug("getFileResource#RelName " + relativeName + " CT: " + contentType);
        return new FileResource(bucket, getPathInDataDir(relativeName), contentType);
    }

    @Override
    public InputStream getFileAsStream(String relativeName) {
        String key = getPathInDataDir(relativeName);

        return bucket.download(key);
    }

    @Override
    public AbstractResource.ResourceResponse respondWithFile(String pathToFile, String contentType) {
        logger.debug("respondWithFile#RelName " + pathToFile + " CT: " + contentType);
        return respondWithFileFromS3(bucket, pathToFile, contentType);
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndReturnPath(AbstractUploadedFile upload, ImageResizingStrategyFactoryService.IImageResizingStrategy resizingStrategy) {
        return saveImageFileAndPreviewAndReturnPath(upload, resizingStrategy, null);
    }

    /**
     * It cleans space of the object given as parameter, if anything with the same name already existed.
     *
     * @param key Object to clean before further usage.
     */
    private void removeObjectIfExists(String key) {
        if (bucket.existsObject(key)) {
            bucket.removeObject(key);
        }
    }

    private void uploadUpdatedImage(BufferedImage image, String fileType, String key) throws IOException {
        ByteArrayOutputStream fullImage = new ByteArrayOutputStream();
        ImageIO.write(image, fileType, fullImage);
        byte[] buffer = fullImage.toByteArray();

        // Save to new file
        InputStream updatedImage = new ByteArrayInputStream(buffer);
        bucket.upload(key, updatedImage);
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(AbstractUploadedFile upload, ImageResizingStrategyFactoryService.IImageResizingStrategy fullImageResizingStrategy, ImageResizingStrategyFactoryService.IImageResizingStrategy previewResizingStrategy) {
        // Determine file type
        String fileType = upload.candidateFileType();
        String fileName = upload.filePath();
        BufferedImage imageGameSized = null;

        // Create a new file
        try {

            String key = getPathInDataDir(fileName);
            BufferedImage sourceImage = ImageIO.read(upload.getInputStream());
            imageGameSized = fullImageResizingStrategy.convertImage(sourceImage);

            // Check new file, delete if it already existed
            removeObjectIfExists(key);

            uploadUpdatedImage(imageGameSized, fileType, key);

            if (previewResizingStrategy != null) {
                // Create preview
                BufferedImage sourcePreviewImage = previewResizingStrategy.convertImage(sourceImage);
                String keyPreview = getFilePreviewInDataDir(fileName);

                uploadUpdatedImage(sourcePreviewImage, fileType, keyPreview);
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
    public String saveFileAndReturnPath(UploadedFile upload) {
        String filePath = upload.filePath();

        try {
            bucket.upload(filePath, upload.getInputStream());
            return bucket.getPublicUrl(filePath);
        } catch (IOException ex) {
            logger.error(ex);
        }

        return null;
    }

    @Override
    public void removeFiles(String key) {
        bucket.removeObject(getPathInDataDir(key));
        bucket.removeObject(getFilePreviewInDataDir(key));
    }
}
