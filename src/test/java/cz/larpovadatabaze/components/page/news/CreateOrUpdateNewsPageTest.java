package cz.larpovadatabaze.components.page.news;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test creation of new piece of news.
 * Test updating of existing piece of news.
 */
public class CreateOrUpdateNewsPageTest extends AcceptanceTest {
    @Autowired
    private CzechMasqueradeBuilder masqueradeBuilder;

    @Test
    public void run() {
        masqueradeBuilder.build();

        tester.startPage(CreateOrUpdateNewsPage.class);
        tester.assertRenderedPage(CreateOrUpdateNewsPage.class);


    }
}
