package cz.larpovadatabaze.components.page.search;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.search.GamesResultsPanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.search.UserResultsPanel;

/**
 *
 */
public class SearchResultsPage extends CsldBasePage {
    public SearchResultsPage(PageParameters params) {
        String query = params.get(SearchBoxPanel.QUERY_PARAMETER_NAME).toString();

        final GamesResultsPanel gamesResultsPanel = new GamesResultsPanel("games", query);
        gamesResultsPanel.setOutputMarkupId(true);
        add(gamesResultsPanel);
        final UserResultsPanel userResultsPanel = new UserResultsPanel("users", query);
        userResultsPanel.setOutputMarkupId(true);
        add(userResultsPanel);
    }
}
