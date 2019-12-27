package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.page.user.CsldSignInPage;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.services.CsldUsers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CreateGameTest extends WithWicket {
    @Autowired
    private CsldUsers users;

    @Test
    public void runAsGuest() {
        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CsldSignInPage.class);
    }

    @Test
    public void runAsUser() {
        TestUtils.logUser(users.getById(3));

        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CreateOrUpdateGamePage.class);

        tester.assertComponent("createOrUpdateGame", CreateOrUpdateGamePanel.class);
    }
}
