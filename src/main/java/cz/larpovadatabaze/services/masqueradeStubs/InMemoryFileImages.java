package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.Images;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class InMemoryFileImages extends InMemoryCrud<Image, Integer> implements Images {
    @Override
    public IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image) {
        return null;
    }

    @Override
    public ResourceReference createImageTypeResourceReference(GenericHibernateDAO<? extends IEntityWithImage, Integer> dao) {
        return null;
    }

    @Override
    public IResource getImageResource(Image image, IEntityWithImage.IPredefinedImage defaultImage) {
        return null;
    }

    @Override
    public IResource getImageResource(IEntityWithImage entity) {
        return null;
    }
}
