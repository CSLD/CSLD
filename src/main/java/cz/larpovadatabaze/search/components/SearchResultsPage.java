package cz.larpovadatabaze.search.components;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.panel.AbstractListGamePanel;
import cz.larpovadatabaze.users.components.panel.AbstractListUserPanel;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class SearchResultsPage extends CsldBasePage {
    public SearchResultsPage(PageParameters params) {
        String query = params.get(SearchBoxPanel.QUERY_PARAMETER_NAME).toString();

        final Component gamesResultsPanel = new AbstractListGamePanel<String>("games", Model.of(query)) {
            @Override
            protected SortableDataProvider<Game, String> getDataProvider() {
                GameSearchProvider res = new GameSearchProvider();
                res.setQuery(query);
                return res;
            }
        };
        gamesResultsPanel.setOutputMarkupId(true);
        add(gamesResultsPanel);

        final Component usersResultsPanel = new AbstractListUserPanel<String>("users", Model.of(query)) {
            @Override
            protected SortableDataProvider<CsldUser, String> getDataProvider() {
                UserSearchProvider res = new UserSearchProvider();
                res.setQuery(query);
                return res;
            }
        };
        add(usersResultsPanel);
    }

    public static PageParameters paramsForSearchResults(String query) {
        PageParameters params = new PageParameters();

        params.add(SearchBoxPanel.QUERY_PARAMETER_NAME, query);

        return params;
    }

}
