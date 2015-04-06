package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;

/**
 * Creates box with information about game
 *
 * User: Michal Kara Date: 14.3.15 Time: 9:26
 */
public class GameBoxPanel extends AbstractCsldPanel<Game> {
    public GameBoxPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = getModelObject();

        // Link
        final BookmarkablePageLink<CsldBasePage> gameLinkContent =
            new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, GameDetail.paramsForGame(game));
        add(gameLinkContent);

        // Rating
        gameLinkContent.add(new GameRatingBoxPanel("gameRating", getModel()));

        // Link && name
        final Label gameName = new Label("gameName", game.getName());
        gameLinkContent.add(gameName);

        // Number of players
        gameLinkContent.add(new Label("players", game.getPlayers()));

        // Comments
        gameLinkContent.add(new Label("comments", game.getAmountOfComments()));

        // Ratings
        gameLinkContent.add(new Label("ratings", game.getAmountOfRatings()));
    }
}
