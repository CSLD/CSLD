package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.Video;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.services.Videos;

public class InMemoryVideos extends InMemoryCrud<Video, Integer> implements Videos {
    @Override
    public String getEmbedingURL(String url) {
        return null;
    }
}
