package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.entities.IEntityWithImage;
import cz.larpovadatabaze.common.entities.Image;
import cz.larpovadatabaze.common.services.CRUDService;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 */
public interface Images extends CRUDService<Image, Integer> {
    // @see getImageResourceReference()
    String RESOURCE_REFERENCE_ID_PARAM_NAME = "id";

    IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image);

    /**
     * Create resource reference for entity type
     *
     * @param dao DAO to use to get entity
     *
     * @return Resource reference for image associated with this type entity. Resource expects entity ID in the parameter
     * named with string in constant RESOURCE_REFERENCE_ID_PARAM_NAME (see above)
     */
    ResourceReference createImageTypeResourceReference(GenericHibernateDAO<? extends IEntityWithImage, Integer> dao);

    IResource getImageResource(Image image, IEntityWithImage.IPredefinedImage defaultImage);

    IResource getImageResource(IEntityWithImage entity);
}
