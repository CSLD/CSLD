package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Panel containing Panel for Adding games and Panel for filtering games. Mainly is used when there is list of games
 * shown.
 */
public class GamesPanel extends Panel {
    public GamesPanel(String id) {
        super(id);

        add(new AddGamePanel("addGame"));
        add(new FilterGamesPanel("filterGames"));
    }
}
