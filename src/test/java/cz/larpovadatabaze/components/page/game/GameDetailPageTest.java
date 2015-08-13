package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests associated with the GameDetailPage.
 */
public class GameDetailPageTest extends AcceptanceTest {
    @Autowired
    private CzechMasqueradeBuilder masqueradeBuilder;

    @Test
    public void run() {
        masqueradeBuilder.build();

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.firstMasquerade));
        tester.assertRenderedPage(GameDetail.class);
    }
}
