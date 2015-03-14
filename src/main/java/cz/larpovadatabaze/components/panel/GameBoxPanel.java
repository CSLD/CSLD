package cz.larpovadatabaze.components.panel;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.DecimalFormat;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;

/**
 * Creates box with information about game
 *
 * User: Michal Kara Date: 14.3.15 Time: 9:26
 */
public class GameBoxPanel extends AbstractCsldPanel<Game> {
    private final static DecimalFormat ratingFormat = new DecimalFormat("0.0");

    public GameBoxPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = getModelObject();

        // Rating
        String gameRatingColor = Rating.getColorOf(game.getTotalRating());
        Label gameRating = new Label("gameRating", ratingFormat.format(game.getTotalRating() / 10));
        gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
        add(gameRating);

        // Link && name
        final BookmarkablePageLink<CsldBasePage> gameLinkContent =
            new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, GameDetail.paramsForGame(game));
        final Label gameName = new Label("gameName", game.getName());
        gameLinkContent.add(gameName);
        add(gameLinkContent);

        // Number of players
        add(new Label("players", game.getPlayers()));

        // Comments
        add(new Label("comments", game.getAmountOfComments()));

        // Ratings
        add(new Label("ratings", game.getAmountOfRatings()));
    }
}
