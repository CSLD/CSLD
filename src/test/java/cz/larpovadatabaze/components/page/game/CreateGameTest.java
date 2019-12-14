package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.components.page.user.CsldSignInPage;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import org.junit.Test;

/**
 *
 */
public class CreateGameTest extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CsldSignInPage.class);
    }

    @Test
    public void runAsUser() {
        TestUtils.logUser(masqueradeBuilder.getUser());

        tester.startPage(CreateOrUpdateGamePage.class);
        tester.assertRenderedPage(CreateOrUpdateGamePage.class);

        tester.assertComponent("createOrUpdateGame", CreateOrUpdateGamePanel.class);
    }
}
