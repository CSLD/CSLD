package cz.larpovadatabaze.games.components.page;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.panel.*;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests associated with the GameDetailPage.
 */
public class GameDetailPageTest extends WithWicket {
    @Autowired
    Games games;
    @Autowired
    CsldUsers users;

    private Game toDisplay;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        toDisplay = games.getById(1);
    }

    @Test
    public void runAsGuest() {
        tester.startPage(GameDetail.class, GameDetail.paramsForGame(toDisplay));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("gameDetail", GameDetailPanel.class);
        tester.assertComponent("ratingsContainerPanel", WebMarkupContainer.class);

        tester.assertComponent("ratingsContainerPanel:ratingsResults", RatingsResultPanel.class);
        tester.assertComponent("ratingsContainerPanel:canNotRatePanel", LoginToRatePanel.class);
        tester.assertVisible("ratingsContainerPanel:canNotRatePanel");

        tester.assertComponent("similarGames", GameListPanel.class);
        tester.assertVisible("similarGames");

        tester.assertComponent("gamesOfAuthors", GameListPanel.class);
        tester.assertVisible("gamesOfAuthors");
    }

    @Test
    public void runAsUser() {
        logUser(users.getById(3));

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(toDisplay));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("ratingsContainerPanel:ratingsPanel", RatingsPanel.class);
        tester.assertVisible("ratingsContainerPanel:ratingsPanel");
    }

    @Test
    public void runAsEditor() {
        logUser(users.getById(2));

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(toDisplay));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("editGamePanel", EditGamePanel.class);
        tester.assertVisible("editGamePanel");

        tester.assertComponent("deleteGamePanel", DeleteGamePanel.class);
        tester.assertVisible("deleteGamePanel");
    }

    @Test
    public void runAsAdministrator() {
        logUser(users.getById(1));

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(toDisplay));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("deleteGamePanel", DeleteGamePanel.class);
        tester.assertVisible("deleteGamePanel");
    }
}
