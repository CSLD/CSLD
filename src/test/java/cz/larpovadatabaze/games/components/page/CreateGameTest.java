package cz.larpovadatabaze.games.components.page;

import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.games.components.panel.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.users.components.page.CsldSignInPage;
import cz.larpovadatabaze.users.services.CsldUsers;
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
