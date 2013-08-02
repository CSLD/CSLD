package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 19:04
 */
public class AddGamePanel extends Panel {
    public AddGamePanel(String id) {
        super(id);
    }

    public AddGamePanel(String id, IModel<Game> model){
        super(id, model);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
