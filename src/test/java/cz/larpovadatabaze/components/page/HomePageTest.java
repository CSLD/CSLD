package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.components.panel.home.LarpCzCalendarPanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.RecentGamesPanel;
import cz.larpovadatabaze.components.panel.home.RecentPhotosPanel;
import org.junit.Test;

/**
 * Test rendering of the HomePage. Validate all panels specific to HomePage are shown.
 */
public class HomePageTest extends AcceptanceTest{
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
