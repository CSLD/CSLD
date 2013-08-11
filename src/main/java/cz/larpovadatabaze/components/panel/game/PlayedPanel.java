package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The Played Panel has three states of being. Either the player does not have any
 * interest in game or the player played the game, or the player is interested in
 * playing the game.
 */
public class PlayedPanel extends Panel {
    @SpringBean
    private UserPlayedGameService userPlayedGameService;

    private String selected = "Nehrál jsem";
    private UserPlayedGame stateOfGame;

    public PlayedPanel(String id, final Game game) {
        super(id);

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        int userId = (logged != null) ? logged.getId() : -1;

        stateOfGame = userPlayedGameService.getUserPlayedGame(game.getId(), userId);
        if(stateOfGame == null) {
            stateOfGame = new UserPlayedGame();
            stateOfGame.setGameId(game.getId());
            stateOfGame.setUserId(userId);
        } else {
            selected = UserPlayedGame.getStateFromDb(stateOfGame.getState());
        }

        Form gameState = new Form("gameState"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                saveState();
                PageParameters params = new PageParameters();
                params.add("id",game.getId());
                setResponsePage(GameDetail.class, params);
            }
        };
        Select states = new Select("stateOfGame", new PropertyModel(this, "selected"));
        states.add(new SelectOption<String>("didntPlay", new StringResourceModel("game.notPlayed", this, null)));
        states.add(new SelectOption<String>("played", new StringResourceModel("game.played", this, null)));
        states.add(new SelectOption<String>("wantToPlay", new StringResourceModel("game.wishToPlay", this, null)));
        states.setOutputMarkupId(true);
        states.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                saveState();
                PageParameters params = new PageParameters();
                params.add("id",game.getId());
                setResponsePage(GameDetail.class, params);
            }
        });

        gameState.add(states);
        add(gameState);
    }

    private void saveState() {
        stateOfGame.setState(UserPlayedGame.getStateForDb(selected));
        userPlayedGameService.saveOrUpdate(stateOfGame);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
