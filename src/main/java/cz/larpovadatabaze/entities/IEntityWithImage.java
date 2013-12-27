package cz.larpovadatabaze.entities;

/**
 * Entity which has image that has some default value
 *
 * User: Michal Kara
 * Date: 26.12.13
 * Time: 23:12
 */
public interface IEntityWithImage {
    // Defailt image interface
    public static interface IPredefinedImage {
        Class getBaseClass();
        String getPath();
    }

    /**
     * @return Image associated with the entity or NULL when default sould be used
     */
    Image getImage();

    /**
     * @return Default image to be used
     */
    IPredefinedImage getDefaultImage();
}
