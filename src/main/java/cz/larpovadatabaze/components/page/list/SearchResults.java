package cz.larpovadatabaze.components.page.list;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 17:09
 */
public class SearchResults extends CsldBasePage {
    public SearchResults() {
        add(new FilterGamesPanel("gamePanel"));

    }
}
