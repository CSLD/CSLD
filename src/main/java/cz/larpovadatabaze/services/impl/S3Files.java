package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.File;
import java.io.FileNotFoundException;

public class S3Files implements FileService {


    @Override
    public String getPathInDataDir(String relativeName) {
        return null;
    }

    @Override
    public String getFilePreviewInDataDir(String relativeName) {
        return null;
    }

    @Override
    public AbstractResource getFileResource(String relativeName, String contentType) throws FileNotFoundException {
        return null;
    }

    @Override
    public AbstractResource.ResourceResponse respondWithFile(String pathToFile, String contentType) {
        return null;
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndReturnPath(FileUpload upload, ImageResizingStrategyFactoryService.IImageResizingStrategy resizingStrategy) {
        return null;
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(FileUpload upload, ImageResizingStrategyFactoryService.IImageResizingStrategy fullImageResizingStrategy, ImageResizingStrategyFactoryService.IImageResizingStrategy previewResizingStrategy) {
        return null;
    }

    @Override
    public void removeFiles(String relativePath) {

    }
}
