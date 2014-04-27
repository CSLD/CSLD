package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.ImageDAO;
import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.util.List;

/**
 *
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

    public IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image) {
        return new PackageResourceReference(image.getBaseClass(), image.getPath()).getResource();
    }

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

    public IResource getImageResource(IEntityWithImage entity) {
        return getImageResource(entity.getImage(), entity.getDefaultImage());
    }

    @Override
    public ResourceReference createImageTypeResourceReference(final GenericHibernateDAO<? extends IEntityWithImage, Integer> dao) {
        return new ResourceReference(ImageService.class, dao.getClass().getSimpleName()) {
            @Override
            public IResource getResource() {
                return new IResource() {
                    @Override
                    public void respond(Attributes attributes) {
                        int id = attributes.getParameters().get(RESOURCE_REFERENCE_ID_PARAM_NAME).toInt();

                        // Load
                        IEntityWithImage entity = dao.findById(id);
                        getImageResource(entity).respond(attributes);
                    }
                };
            }
        };
    }
}
