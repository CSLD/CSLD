package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 18:30
 */
public class UsersPanel extends Panel {
    public UsersPanel(String id) {
        super(id);

        add(new AddGamePanel("addGame"));
        add(new MostActiveCommenter("mostCommentedGames"));
    }
}
