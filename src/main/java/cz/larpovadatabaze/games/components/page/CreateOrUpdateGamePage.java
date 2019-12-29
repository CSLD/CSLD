package cz.larpovadatabaze.games.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.games.components.panel.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This page allows User to create new or update existing game.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateGamePage extends CsldBasePage {
    @SpringBean
    Games games;

    /**
     * Model for game specified by game id
     */
    private class GameModel extends LoadableDetachableModel<Game> {

        // Game id. We could also store id as page property.
        private Integer gameId;

        private GameModel(Integer gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Game load() {
            if (gameId == null) return Game.getEmptyGame(); // Empty game

            Game game = games.getById(gameId);
            if(HbUtils.isProxy(game)){
            }    game = HbUtils.deproxy(game);


            return game;
        }

        @Override
        public void detach() {
            if (gameId != null) {
                // Detach only when not creating a new game
                super.detach();
            }
        }
    }

    public CreateOrUpdateGamePage(PageParameters params){
        if(!params.isEmpty()) setDefaultModel(new GameModel(params.get("id").to(Integer.class)));
        else setDefaultModel(new GameModel(null));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CreateOrUpdateGamePanel("createOrUpdateGame", (GameModel)getDefaultModel()) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                Game game = (Game) object;
                CsldAuthenticatedWebSession session = CsldAuthenticatedWebSession.get();
                session.requestClear();
                throw new RestartResponseException(GameDetail.class, GameDetail.paramsForGame(game));
            }
        });
    }
}
