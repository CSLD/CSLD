package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.Video;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Videos;

public class InMemoryVideos extends InMemoryCrud<Video, Integer> implements Videos {
    @Override
    public String getEmbedingURL(String url) {
        return null;
    }
}
