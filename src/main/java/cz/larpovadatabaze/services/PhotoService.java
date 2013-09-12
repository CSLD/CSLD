package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Photo;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 6.9.13
 * Time: 22:58
 */
public interface PhotoService extends GenericService<Photo> {
    boolean saveOrUpdate(Photo actualPhoto);
}
