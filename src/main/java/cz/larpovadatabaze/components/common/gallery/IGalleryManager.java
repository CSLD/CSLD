package cz.larpovadatabaze.components.common.gallery;

import java.io.Serializable;

/**
 * Provides callbacks for gallery management functions
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 16:11
 */
public interface IGalleryManager extends Serializable {

    /**
     * Set new image description
     *
     * @param imageId ID of image
     * @param newDescription Image description
     */
    public void setImageDescription(int imageId, String newDescription);

    /**
     * Delete image
     *
     * @param imageId ID of image
     */
    public void deleteImage(int imageId);

    /**
     * Set order of images in the gallery
     *
     * @param imageIds Image IDs in the desired order. Application should gracefully handle situations when list contains some IDs that no longer exists or does not include some existung IDs (concurrent operations)
     */
    public void setImageOrder(Iterable<Integer> imageIds);

    /**
     * It publishes photo on the home page. Default state for photo is that it isn't published.
     *
     * @param id Id of the image
     */
    void publishPhoto(int id);

    /**
     * It hides photo from the front page.
     *
     * @param id Id of the image
     */
    void hidePhotoFromFront(int id);
}
