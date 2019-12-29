package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.Video;
import cz.larpovadatabaze.common.services.CRUDService;

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
