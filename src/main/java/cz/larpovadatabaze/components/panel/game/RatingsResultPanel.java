package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This panel shows result of rating of a game. It has background based on the rating and the amount of rating as
 * number.
 */
public class RatingsResultPanel extends Panel {
    @SpringBean
    private GameService gameService;

    public RatingsResultPanel(String id, Game game) {
        super(id);

        double ratingOfGame = gameService.getRatingOfGame(game);
        String ratingColor = Rating.getColorOf(ratingOfGame);

        WebMarkupContainer finalRating = new WebMarkupContainer("resultsOfRating");
        finalRating.add(new AttributeAppender("class",Model.of(ratingColor)));

        Label rating = new Label("rating", Model.of(ratingOfGame));
        Label raters = new Label("raters", Model.of(game.getRatings().size()));

        finalRating.add(rating);
        finalRating.add(raters);
        add(finalRating);
    }
}
