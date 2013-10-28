package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows basic statistics about larp database.
 */
public class StatisticsPanel extends Panel {
    @SpringBean
    GameService gameService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    CommentService commentService;
    @SpringBean
    RatingService ratingService;

    public StatisticsPanel(String id) {
        super(id);

        int amountOfUsers = csldUserService.getAmountOfAuthors();
        int amountOfAuthors = csldUserService.getAmountOfOnlyAuthors();
        int amountOfComments = commentService.getAmountOfComments();
        int amountOfLarps = gameService.getAmountOfGames();
        int amountOfRatings = ratingService.getAmountOfRatings();
        int averageRating = (int) (ratingService.getAverageRating() * 10);

        add(new Label("amountOfUsers", Model.of(amountOfUsers)));
        add(new Label("amountOfAuthors", Model.of(amountOfAuthors)));
        add(new Label("amountOfComments", Model.of(amountOfComments)));
        add(new Label("amountOfLarps", Model.of(amountOfLarps)));
        add(new Label("amountOfRatings", Model.of(amountOfRatings)));
        add(new Label("averageRating", Model.of(averageRating)));
    }
}
