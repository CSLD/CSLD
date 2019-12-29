package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.page.GameDetail;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;

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
                new BookmarkablePageLink<>("gameLink", GameDetail.class, GameDetail.paramsForGame(game));
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
