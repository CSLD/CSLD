package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This page allows User to create new or update existing game.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateGamePage extends CsldBasePage {
    @SpringBean
    GameService gameService;

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

            Game game = gameService.getById(gameId);
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
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                Game game = (Game) form.getModelObject();
                CsldAuthenticatedWebSession session = (CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get();
                session.requestClear();
                throw new RestartResponseException(GameDetail.class, GameDetail.paramsForGame(game));
            }
        });
    }
}
