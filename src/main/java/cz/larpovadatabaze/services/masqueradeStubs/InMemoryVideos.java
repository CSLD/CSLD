package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.entities.Video;
import cz.larpovadatabaze.services.Videos;

public class InMemoryVideos extends InMemoryCrud<Video, Integer> implements Videos {
    @Override
    public String getEmbedingURL(String url) {
        return null;
    }
}
