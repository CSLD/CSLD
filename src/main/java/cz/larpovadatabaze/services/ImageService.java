package cz.larpovadatabaze.services;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 */
public interface ImageService extends GenericService<Image> {
    // @see getImageResourceReference()
    public static final String RESOURCE_REFERENCE_ID_PARAM_NAME="id";

    public boolean insert(Image image);

    public IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image);


    /**
     * Create resource reference for entity type
     *
     * @param dao DAO to use to get entity
     *
     * @return Resource reference for image associated with this type entity. Resource expects entity ID in the parameter
     * named with string in constant RESOURCE_REFERENCE_ID_PARAM_NAME (see above)
     */
    public ResourceReference createImageTypeResourceReference(GenericHibernateDAO<? extends IEntityWithImage, Integer> dao);
}
