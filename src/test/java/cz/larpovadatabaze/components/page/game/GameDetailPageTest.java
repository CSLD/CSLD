package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import static cz.larpovadatabaze.TestUtils.logUser;

/**
 * Tests associated with the GameDetailPage.
 */
public class GameDetailPageTest extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.getFirstMasquerade()));
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
        logUser(masqueradeBuilder.getUser());

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.getFirstMasquerade()));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("ratingsContainerPanel:ratingsPanel", RatingsPanel.class);
        tester.assertVisible("ratingsContainerPanel:ratingsPanel");
    }

    @Test
    public void runAsEditor() {
        logUser(masqueradeBuilder.getEditor());

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.getFirstMasquerade()));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("editGamePanel", EditGamePanel.class);
        tester.assertVisible("editGamePanel");

        tester.assertComponent("deleteGamePanel", DeleteGamePanel.class);
        tester.assertVisible("deleteGamePanel");
    }

    @Test
    public void runAsAdministrator() {
        logUser(masqueradeBuilder.getAdministrator());

        tester.startPage(GameDetail.class, GameDetail.paramsForGame(masqueradeBuilder.getFirstMasquerade()));
        tester.assertRenderedPage(GameDetail.class);

        tester.assertComponent("deleteGamePanel", DeleteGamePanel.class);
        tester.assertVisible("deleteGamePanel");
    }
}
