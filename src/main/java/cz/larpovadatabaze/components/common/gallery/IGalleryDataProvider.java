package cz.larpovadatabaze.components.common.gallery;

import org.apache.wicket.request.resource.AbstractResource;

import java.io.Serializable;

/**
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 17:13
 */
public interface IGalleryDataProvider extends Serializable {
    /**
     * @param imageId Image id
     *
     * @return Image preview response
     */
    public AbstractResource.ResourceResponse getImagePreview(int imageId);

    /**
     * @param imageId Image id
     *
     * @return Full image response
     */
    public AbstractResource.ResourceResponse getFullImage(int imageId);

}
