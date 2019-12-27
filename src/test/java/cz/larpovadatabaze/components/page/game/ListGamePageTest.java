package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.panel.game.FilterGameTabsPanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesSidePanel;
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
