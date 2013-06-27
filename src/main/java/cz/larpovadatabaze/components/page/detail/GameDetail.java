package cz.larpovadatabaze.components.page.detail;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.GameDetailPanel;
import cz.larpovadatabaze.components.panel.game.EditGamePanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 11:31
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

        /*PlayedPanel played = new PlayedPanel("playedPanel");
        add(played);*/

        add(new GameDetailPanel("gameDetail", game));

        /*
        ResultsRatingsPanel resultsRatingsPanel = new ResultsRatingsPanel("resultRatingsPanel");
        add(resultsRatingsPanel);

        if(CsldAuthenticatedWebSession.get().isSignedIn()){
            add(new RatingsPanel("ratingsPanel", game.getId()));
        } else {
            add(new CanNotRatePanel("ratingsPanel"));
        }*/

        EditGamePanel editGamePanel = new EditGamePanel("editGamePanel", game);
        add(editGamePanel);

        List<Game> similarGames = gameService.getSimilar(game);
        add(new GameListPanel("similarGames", similarGames));

        List<Game> gamesOfAuthors = gameService.gamesOfAuthors(game);
        add(new GameListPanel("gamesOfAuthors", gamesOfAuthors));
    }
}
