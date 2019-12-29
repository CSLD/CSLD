package cz.larpovadatabaze.components.page.search;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.games.components.panel.AbstractListGamePanel;
import cz.larpovadatabaze.search.components.SearchResultsPage;
import org.junit.Test;

/**
 *
 */
public class SearchResultsPageTest extends WithWicket {
    @Test
    public void runAsGuest() {
        tester.startPage(SearchResultsPage.class, SearchResultsPage.paramsForSearchResults("masq"));
        tester.assertRenderedPage(SearchResultsPage.class);

        tester.assertComponent("games", AbstractListGamePanel.class);
    }
}
