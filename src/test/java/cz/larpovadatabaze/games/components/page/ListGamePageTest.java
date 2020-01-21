package cz.larpovadatabaze.games.components.page;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.games.components.panel.FilterGameTabsPanel;
import cz.larpovadatabaze.games.components.panel.FilterGamesSidePanel;
import org.junit.Test;

/**
 *
 */
public class ListGamePageTest extends WithWicket {
    @Test
    public void runAsGuest() {
        tester.startPage(ListGamePage.class);
        tester.assertRenderedPage(ListGamePage.class);

        tester.assertComponent("filterTabs", FilterGameTabsPanel.class);
        tester.assertComponent("filterGames", FilterGamesSidePanel.class);
    }
}
