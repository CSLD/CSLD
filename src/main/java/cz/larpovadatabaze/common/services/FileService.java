package cz.larpovadatabaze.common.services;

import cz.larpovadatabaze.common.models.AbstractUploadedFile;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService.IImageResizingStrategy;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Service for working with uploaded files
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 20:41
 */
public interface FileService {
    String PREVIEW_POSTFIX = "-p";

    /**
     * Return class from saveImageFileAndReturnPath
     */
    class ResizeAndSaveReturn {
        public final String path;
        public final int savedWidth;
        public final int savedHeight;

        public ResizeAndSaveReturn(String path, int savedWidth, int savedHeight) {
            this.path = path;
            this.savedWidth = savedWidth;
            this.savedHeight = savedHeight;
        }
    }

    /**
     * @param relativeName Relative name
     *
     * @return File object for the specified name in CSLD's data directory
     */
    String getPathInDataDir(String relativeName);

    /**
     * @param relativeName Relative name
     *
     * @return File object for preview for the specified name in CSLD's data directory
     */
    String getFilePreviewInDataDir(String relativeName);

    /**
     * @param relativeName Relative file name
     * @param contentType File content type (if known)
     *
     * @return Resource reference for the file
     */
    AbstractResource getFileResource(String relativeName, String contentType) throws FileNotFoundException;

    /**
     * @param name
     *
     * @return Stream with file contents
     */
    InputStream getFileAsStream(String name) throws FileNotFoundException;

    /**
     * Respond to resource request with the given file
     *
     * @param pathToTheFile Path to the file to the output.
     * @param contentType Content type (if known)
     *
     * @return Resonse that will produce file data
     */
    AbstractResource.ResourceResponse respondWithFile(String pathToTheFile, String contentType);

    /**
     * Resize and save uploaded image
     *
     * @param upload           Upload to save
     * @param resizingStrategy Strategy to use to resize the image
     * @return Result class
     */
    ResizeAndSaveReturn saveImageFileAndReturnPath(AbstractUploadedFile upload, IImageResizingStrategy resizingStrategy);

    /**
     * Resize and save uploaded image plus save image preview
     *
     * @param upload                    Upload to save
     * @param fullImageResizingStrategy Strategy used to resize the full image
     * @param previewResizingStrategy   Strategy used to resize image preview
     * @return Result class
     */
    ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(AbstractUploadedFile upload, IImageResizingStrategy fullImageResizingStrategy, IImageResizingStrategy previewResizingStrategy);

    /**
     * Save any file to the storage.
     *
     * @param blueprintFile File to be upload.
     * @return Path in the storage
     */
    String saveFileAndReturnPath(UploadedFile blueprintFile);

    /**
     * Delete file(s) belonging to this relative path (used when file is about to be deleted)
     *
     * @param relativePath Path to be deleted
     */
    void removeFiles(String relativePath);
}
