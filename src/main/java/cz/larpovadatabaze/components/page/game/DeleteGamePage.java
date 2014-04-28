package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Does not have markup, only takes care of deleting game.
 */
@AuthorizeInstantiation({"Admin","Editor"})
public class DeleteGamePage extends CsldBasePage {
    @SpringBean
    GameService gameService;

    public DeleteGamePage(PageParameters params) {
        Integer gameId;
        try {
            gameId = params.get("id").to(Integer.class);
        } catch(NumberFormatException ex) {
            throw new RestartResponseException(ListGame.class);
        }

        gameService.hideGame(gameId);
        throw new RestartResponseException(ListGame.class);
    }
}
