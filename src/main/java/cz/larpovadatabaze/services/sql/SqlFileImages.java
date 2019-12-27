package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.Images;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;

/**
 *
 */
@Repository
public class SqlFileImages extends CRUD<Image, Integer> implements Images {
    FileService fileService;

    public SqlFileImages(SessionFactory sessionFactory, FileService fileService) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Image.class)));
        this.fileService = fileService;
    }

    @Override
    public void remove(Image toRemove) {
        crudRepository.makeTransient(toRemove);

        // Delete file(s)
        fileService.removeFiles(toRemove.getPath());
    }

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

    @Override
    public ResourceReference createImageTypeResourceReference(final GenericHibernateDAO<? extends IEntityWithImage, Integer> dao) {
        return new ResourceReference(Images.class, dao.getClass().getSimpleName()) {
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
