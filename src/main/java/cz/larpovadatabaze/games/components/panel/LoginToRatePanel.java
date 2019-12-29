package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * This panel is shown when user does not have right to rate the game.
 * Therefore when he is not logged.
 */
public class LoginToRatePanel extends Panel {
    public LoginToRatePanel(String id) {
        super(id);
    }

    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(!CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
