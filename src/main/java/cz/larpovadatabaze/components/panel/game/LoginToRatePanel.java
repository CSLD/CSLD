package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.markup.html.panel.Panel;

import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;

/**
 * This panel is shown when user does not have right to rate the game.
 * Therefore when he is not logged.
 */
public class LoginToRatePanel extends Panel {
    public LoginToRatePanel(String id) {
        super(id);
    }

    protected void onConfigure() {
        setVisibilityAllowed(!CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
