package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.games.services.Videos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SqlVideosIT extends WithDatabase {
    private Videos underTest;

    @BeforeEach
    public void prepareVideos() {
        underTest = new SqlVideos(sessionFactory);
    }

    @Test
    public void getValidEmbeddedUrlForYouTube() {
        String result = underTest.getEmbedingURL("https://youtu.be/hB5LZaQgNfM");

        assertThat(result, is("//www.youtube.com/embed/hB5LZaQgNfM"));
    }
}
