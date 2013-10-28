package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * This panel only contains header and panel for adding game and it shows use, who commented most games.
 */
public class UsersPanel extends Panel {
    public UsersPanel(String id) {
        super(id);

        add(new AddGamePanel("addGame"));
        add(new MostActiveCommenter("mostCommentedGames"));
    }
}
