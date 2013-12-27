package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.IEntityWithImage;
import cz.larpovadatabaze.entities.Image;
import org.apache.wicket.request.resource.IResource;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 19:00
 */
public interface ImageService extends GenericService<Image> {

    public boolean insert(Image image);

    /**
     * @param image Image to retrieve
     *
     * @return Resource reference for the image
     */
    public IResource getPredefinedImageResource(IEntityWithImage.IPredefinedImage image);

    /**
     * @param image Image to retrieve (may be NULL)
     * @param defaultImage Default image to use when specified image is NULL
     *
     * @return Resource reference for the image
     */
    public IResource getImageResource(Image image, IEntityWithImage.IPredefinedImage defaultImage);

    /**
     * Get associated image resource for entity that is IEntityWithImage
     *
     * @param entity Entity get image for
     * @return Image resource for the user
     */
    public IResource getImageResource(IEntityWithImage entity);
}
