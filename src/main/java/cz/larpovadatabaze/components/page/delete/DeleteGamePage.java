package cz.larpovadatabaze.components.page.delete;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.list.ListGame;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 13:09
 */
public class DeleteGamePage extends CsldBasePage {
    @SpringBean
    private GameService gameService;
    @SpringBean
    private CommentService commentService;
    @SpringBean
    private RatingService ratingService;

    public DeleteGamePage(PageParameters params) {
        Integer gameId = params.get("id").to(Integer.class);

        Game game = gameService.getById(gameId);
        if(game != null) {
            List<Comment> comments = game.getComments();
            for(Comment comment : comments){
                commentService.remove(comment);
            }
            List<Rating> ratings= game.getRatings();
            for(Rating rating: ratings){
                ratingService.remove(rating);
            }
            gameService.remove(game);
        }

        throw new RestartResponseException(ListGame.class);
    }
}
