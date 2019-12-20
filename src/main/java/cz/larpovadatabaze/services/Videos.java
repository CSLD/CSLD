package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Video;

/**
 *
 */
public interface Videos extends CRUDService<Video, Integer> {
    /**
     * Process URL and try to get URL for video embeding
     *
     * @param url URL entered by user
     * @return URL for video embeding. NULL when URL has bad format. Empty string when input was empty.
     */
    String getEmbedingURL(String url);
}
