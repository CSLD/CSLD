package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Video;

/**
 *
 */
public interface VideoService extends GenericService<Video> {
    public void saveOrUpdate(Video video);
}
