package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
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
    Ratings ratings;

    private final Component[] componentsToRefresh;
    private final int gameId;
    private final IModel<Rating> model;
    private final IModel<Game> gameModel;

    /**
     * Model for user played game - implemented as loadable / detachable
     */
    private class UserPlayedGameModel implements IModel<Rating> {
        @Override
        public Rating getObject() {
            int userId = getUserId();
            Rating stateOfGame = ratings.getUserRatingOfGame(userId, gameId);
            if (stateOfGame == null) {
                stateOfGame = new Rating();
                stateOfGame.setGame(gameModel.getObject());
                stateOfGame.setUser(getUser());
            }

            return stateOfGame;
        }
    }

    /**
     * Model that returns "active" string when user played game state is equal to state passed in constructor
     */
    private class StateActiveModel implements IModel<String> {
        private Rating.GameState state;

        private StateActiveModel(Rating.GameState state) {
            this.state = state;
        }

        @Override
        public String getObject() {
            return model.getObject().getStateEnum().equals(state) ? "btn-primary" : "btn-default";
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
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        return (logged != null) ? logged.getId() : -1;
    }

    protected CsldUser getUser(){
        return CsldAuthenticatedWebSession.get().getLoggedUser();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        AjaxLink<Rating> didntPlay = new StateAjaxLink<>("didntPlay", Rating.GameState.NONE);
        AjaxLink<Rating> played = new StateAjaxLink<>("played", Rating.GameState.PLAYED);
        AjaxLink<Rating> wantToPlay = new StateAjaxLink<>("wantToPlay", Rating.GameState.WANT_TO_PLAY);

        String CSS_CLASS = "class";
        /* Add attribute modifiers to buttons */
        played.add(new AttributeAppender(CSS_CLASS, new StateActiveModel(Rating.GameState.PLAYED), " "));
        wantToPlay.add(new AttributeAppender(CSS_CLASS, new StateActiveModel(Rating.GameState.WANT_TO_PLAY), " "));
        didntPlay.add(new AttributeAppender(CSS_CLASS, new StateActiveModel(Rating.GameState.NONE), " "));

        add(didntPlay);
        add(played);
        add(wantToPlay);
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    private class StateAjaxLink<T> extends AjaxLink<T> {
        private Rating.GameState state;

        public StateAjaxLink(String id, Rating.GameState state) {
            super(id);

            this.state = state;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            saveStateAndReload(target, state);
        }

        private void saveStateAndReload(AjaxRequestTarget target, Rating.GameState state) {
            // Update in DB
            if (state != Rating.GameState.NONE) {
                Rating stateOfGame = model.getObject();
                stateOfGame.setStateEnum(state);
                stateOfGame.setUser((CsldAuthenticatedWebSession.get()).getLoggedUser());
                ratings.saveOrUpdate(stateOfGame);
            } else {
                ratings.remove(model.getObject());
            }

            // Refresh model and components and gameModel
            gameModel.detach();

            // Clean empty placeholders from componentsToRefresh.
            List<Component> actComponents = new ArrayList<Component>();
            for (Component component : componentsToRefresh) {
                if (component != null) {
                    actComponents.add(component);
                }
            }
            target.add(actComponents.toArray(new Component[]{}));
        }
    }
}