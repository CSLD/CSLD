package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * The Played Panel has three states of being. Either the player does not have any
 * interest in game or the player played the game, or the player is interested in
 * playing the game.
 */
public class PlayedPanel extends Panel {
    @SpringBean
    UserPlayedGameService userPlayedGameService;
    @SpringBean
    RatingService ratingService;

    private final Component[] componentsToRefresh;
    private final int gameId;
    private final IModel<UserPlayedGame> model;
    private final IModel<Game> gameModel;

    private AjaxLink<UserPlayedGame> didntPlay;
    private AjaxLink<UserPlayedGame> played;
    private AjaxLink<UserPlayedGame> wantToPlay;

    /**
     * Model for user played game - implemented as loadable / detachable
     */
    private class UserPlayedGameModel extends AbstractReadOnlyModel<UserPlayedGame> {
        @Override
        public UserPlayedGame getObject() {
            int userId = getUserId();
            UserPlayedGame stateOfGame = userPlayedGameService.getUserPlayedGame(gameId, userId);
            if(stateOfGame == null) {
                stateOfGame = new UserPlayedGame();
                stateOfGame.setGameId(gameId);
                stateOfGame.setUserId(userId);
            }

            return stateOfGame;
        }
    }

    /**
     * Model that returns "active" string when user played game state is equal to state passed in constructor
     */
    private class StateActiveModel extends AbstractReadOnlyModel<String> {
        private UserPlayedGame.UserPlayedGameState state;

        private StateActiveModel(UserPlayedGame.UserPlayedGameState state) {
            this.state = state;
        }

        @Override
        public String getObject() {
            return model.getObject().getStateEnum().equals(state)?"active":"";
        }
    }

    public PlayedPanel(String id, IModel<Game> gameModel, Component[] componentsToRefresh) {
        super(id);

        this.gameId = gameModel.getObject().getId();
        this.model = new UserPlayedGameModel();
        this.gameModel = gameModel;
        this.componentsToRefresh = componentsToRefresh;
    }

    protected int getUserId() {
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        return (logged != null) ? logged.getId() : -1;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        didntPlay = new AjaxLink<UserPlayedGame>("didntPlay") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saveStateAndReload(target, UserPlayedGame.UserPlayedGameState.NONE);
            }
        };
        played = new AjaxLink<UserPlayedGame>("played") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saveStateAndReload(target, UserPlayedGame.UserPlayedGameState.PLAYED);
            }
        };
        wantToPlay = new AjaxLink<UserPlayedGame>("wantToPlay") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saveStateAndReload(target, UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY);
            }
        };

        /* Add attribute modifiers to buttons */
        played.add(new AttributeAppender("class", new StateActiveModel(UserPlayedGame.UserPlayedGameState.PLAYED), " "));
        wantToPlay.add(new AttributeAppender("class", new StateActiveModel(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY), " "));
        didntPlay.add(new AttributeAppender("class", new StateActiveModel(UserPlayedGame.UserPlayedGameState.NONE), " "));

        add(didntPlay);
        add(played);
        add(wantToPlay);
    }

    private void saveStateAndReload(AjaxRequestTarget target, UserPlayedGame.UserPlayedGameState state) {
        // Update in DB
        UserPlayedGame stateOfGame = model.getObject();
        stateOfGame.setStateEnum(state);
        stateOfGame.setPlayerOfGame((CsldAuthenticatedWebSession.get()).getLoggedUser());
        userPlayedGameService.saveOrUpdate(stateOfGame);

        // Refresh model and components and gameModel
        gameModel.detach();
        // Clean empty placeholders from componentsToRefresh.
        List<Component> actComponents = new ArrayList<Component>();
        for(Component component: componentsToRefresh) {
            if(component != null) {
                actComponents.add(component);
            }
        }
        target.add(actComponents.toArray(new Component[]{}));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }}
