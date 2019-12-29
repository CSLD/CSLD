package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.common.components.home.LarpCzCalendarPanel;
import cz.larpovadatabaze.common.components.home.LastCommentsPanel;
import cz.larpovadatabaze.common.components.home.RecentGamesPanel;
import cz.larpovadatabaze.common.components.home.RecentPhotosPanel;
import cz.larpovadatabaze.common.components.page.HomePage;
import org.junit.Test;

/**
 * Test rendering of the HomePage. Validate all panels specific to HomePage are shown.
 */
public class HomePageTest extends WithWicket {
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
