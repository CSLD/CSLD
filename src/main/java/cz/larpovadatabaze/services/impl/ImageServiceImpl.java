package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.ImageDAO;
import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 19:02
 */
@Repository
public class ImageServiceImpl implements ImageService {
    @Autowired
    ImageDAO imageDAO;

    @Autowired
    FileService fileService;

    @Override
    public boolean insert(Image image) {
        return imageDAO.saveOrUpdate(image);
    }

    @Override
    public List<Image> getAll() {
        return imageDAO.findAll();
    }

    @Override
    public List<Image> getUnique(Image example) {
        return imageDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(Image toRemove) {
        imageDAO.makeTransient(toRemove);

        // Delete file(s)
        fileService.removeFiles(toRemove.getPath());
    }

    @Override
    public List<Image> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image) {
        return new PackageResourceReference(image.getBaseClass(), image.getPath()).getResource();
    }

    @Override
    public IResource getImageResource(Image image, IEntityWithImage.IPredefinedImage defaultImage) {
        if (image != null) {
            try {
                return fileService.getFileResource(image.getPath(), image.getContentType());
            }
            catch(FileNotFoundException e) {
                // Not found - return predefined
                return getPredefinedImageResource(defaultImage);
            }
        }
        else return getPredefinedImageResource(defaultImage);
    }

    @Override
    public IResource getImageResource(IEntityWithImage entity) {
        return getImageResource(entity.getImage(), entity.getDefaultImage());
    }
}
