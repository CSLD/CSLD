package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class GameDetail extends CsldBasePage {
    @SpringBean
    private GameService gameService;

    public GameDetail(PageParameters params){
        Integer gameId = params.get("id").to(Integer.class);
        Game game = gameService.getById(gameId);
        if(HbUtils.isProxy(game)){
            game = HbUtils.deproxy(game);
        }

        add(new PlayedPanel("playedPanel", game));
        add(new GameDetailPanel("gameDetail", game));
        add(new CommentsPanel("addComment", game));
        add(new CommentsListPanel("commentsList", game));

        add(new RatingsResultPanel("ratingsResults", game));
        add(new CanNotRatePanel("canNotRatePanel"));
        add(new RatingsPanel("ratingsPanel", game.getId()));

        EditGamePanel editGamePanel = new EditGamePanel("editGamePanel", game);
        add(editGamePanel);

        List<Game> similarGames = gameService.getSimilar(game);
        add(new GameListPanel("similarGames", similarGames));

        List<Game> gamesOfAuthors = gameService.gamesOfAuthors(game);
        add(new GameListPanel("gamesOfAuthors", gamesOfAuthors));
    }
}
