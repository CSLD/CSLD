package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jbalhar on 18. 8. 2015.
 */
public class GameDetailPageUserTest extends AcceptanceTest {
    @Autowired
    private CzechMasqueradeBuilder masqueradeBuilder;

    @Before
    public void setUp() {
        super.setUp();

        masqueradeBuilder.build();

        TestUtils.logUser(masqueradeBuilder.user);
        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.getFirstMasquerade()));
        tester.clickLink("");
    }

    @Test
    public void rateActualGame() {
        // Tester click on the star with rating.
        // Assert rating for this game was added.
    }

    @Test
    public void commentActualGame() {

    }

    @Test
    public void addPlayedToGame() {

    }

    @Test
    public void addAsWantToPlay() {

    }

    @Test
    public void contactPlayersOfLarp() {

    }

    @Test
    public void contactAuthorOfTheLarp() {

    }
}
