package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 19:00
 */
public interface ImageService extends GenericService<Image> {
    public void insert(Image image);
}
