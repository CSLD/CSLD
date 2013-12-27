package cz.larpovadatabaze.services;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Service for working with uploaded files
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 20:41
 */
public interface FileService {
    /**
     * Return class from saveImageFileAndReturnPath
     */
    public static class ResizeAndSaveReturn {
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
    public File getPathInDataDir(String relativeName);

    /**
     * @param relativeName Relative name
     *
     * @return File object for preview for the specified name in CSLD's data directory
     */
    public File getFilePreviewInDataDir(String relativeName);

    /**
     * @param relativeName Relative file name
     * @param contentType File content type (if known)
     *
     * @return Resource reference for the file
     */
    public AbstractResource getFileResource(String relativeName, String contentType) throws FileNotFoundException;

    /**
     * Respond to resource request with the given file
     *
     * @param relativeName Name of file to output
     * @param contentType Content type (if known)
     *
     * @return Resonse that will produce file data
     */
    public AbstractResource.ResourceResponse respondWithFile(String relativeName, String contentType);

    /**
     * Respond to resource request with the given file
     *
     * @param file File to output
     * @param contentType Content type (if known)
     *
     * @return Resonse that will produce file data
     */
    public AbstractResource.ResourceResponse respondWithFile(File file, String contentType);

    /**
     * Resize and save uploaded image
     *
     * @param upload Upload to save
     * @param maxHeight Maximum width
     * @param maxWidth Maximum height
     *
     * @return Result class
     */
    public ResizeAndSaveReturn saveImageFileAndReturnPath(FileUpload upload, int maxHeight, int maxWidth);

    /**
     * Resize and save uploaded image plus save image preview
     *
     * @param upload Upload to save
     * @param maxHeight Maximum width
     * @param maxWidth Maximum height
     * @param previewSize Size of (rectangular) preview
     *
     * @return Result class
     */
    public ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(FileUpload upload, int maxHeight, int maxWidth, int previewSize);

    /**
     * Delete file(s) belonging to this relative path (used when file is about to be deleted)
     *
     * @param relativePath Path to be deleted
     */
    public void removeFiles(String relativePath);
 }
