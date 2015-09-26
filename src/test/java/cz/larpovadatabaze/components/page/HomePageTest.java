package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.panel.home.LarpCzCalendarPanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.RecentGamesPanel;
import cz.larpovadatabaze.components.panel.home.RecentPhotosPanel;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test rendering of the HomePage. Validate all panels specific to HomePage are shown.
 */
public class HomePageTest extends AcceptanceTest{
    @Autowired
    private CzechMasqueradeBuilder masqueradeBuilder;

    @Override
    public void setUp() {
        super.setUp();

        masqueradeBuilder.build();
    }

    @Test
    public void homePageIsRendered() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);

        tester.assertComponent("recentGames", RecentGamesPanel.class);
        tester.assertComponent("calendar", LarpCzCalendarPanel.class);
        tester.assertComponent("lastComments", LastCommentsPanel.class);
        tester.assertComponent("recentPhotos", RecentPhotosPanel.class);
    }
}
