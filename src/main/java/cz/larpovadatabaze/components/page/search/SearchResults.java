package cz.larpovadatabaze.components.page.search;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import cz.larpovadatabaze.components.panel.search.GamesResultsPanel;
import cz.larpovadatabaze.components.panel.search.UserResultsPanel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 */
public class SearchResults extends CsldBasePage {
    public SearchResults(PageParameters params) {
        String query = params.get("queryString").toString();

        Image searchIcon = new Image("searchIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getSearchResultsIconPath()));
        add(searchIcon);

        add(new GamesResultsPanel("games", query));
        add(new UserResultsPanel("users", query));

        add(new FilterGamesPanel("gamePanel"));

    }
}
