package cz.larpovadatabaze.components.page.search;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.components.panel.game.AbstractListGamePanel;
import org.junit.Test;

/**
 *
 */
public class SearchResultsPageTest extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        tester.startPage(SearchResultsPage.class, SearchResultsPage.paramsForSearchResults("masq"));
        tester.assertRenderedPage(SearchResultsPage.class);

        tester.assertComponent("games", AbstractListGamePanel.class);
    }
}
