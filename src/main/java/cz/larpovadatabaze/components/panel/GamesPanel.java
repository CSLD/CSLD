package cz.larpovadatabaze.components.panel;

import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 30.4.13
 * Time: 13:09
 */
public class GamesPanel extends Panel {
    public GamesPanel(String id) {
        super(id);

        add(new AddGamePanel("addGame"));
        add(new FilterGamesPanel("filterGames"));
    }
}
