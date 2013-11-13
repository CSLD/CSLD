package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The Played Panel has three states of being. Either the player does not have any
 * interest in game or the player played the game, or the player is interested in
 * playing the game.
 */
public abstract class PlayedPanel extends Panel {
    @SpringBean
    UserPlayedGameService userPlayedGameService;
    @SpringBean
    RatingService ratingService;

    private String selected = "Nehrál jsem";
    private UserPlayedGame stateOfGame;

    private AjaxLink<UserPlayedGame> didntPlay;
    private AjaxLink<UserPlayedGame> played;
    private AjaxLink<UserPlayedGame> wantToPlay;

    private Game game;

    @SuppressWarnings("unchecked")
    public PlayedPanel(String id, final Game game) {
        super(id);

        setOutputMarkupId(true);
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        final int userId = (logged != null) ? logged.getId() : -1;
        this.game = game;

        didntPlay = new AjaxLink<UserPlayedGame>("didntPlay") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                selected = "Nehrál jsem";
                played.add(AttributeModifier.replace("class", Model.of("played button")));
                wantToPlay.add(AttributeModifier.replace("class", Model.of("wantToPlay button")));
                didntPlay.add(AttributeModifier.replace("class", Model.of("active didnt button")));

                try {
                    Rating rating = ratingService.getUserRatingOfGame(userId, game.getId());
                    if(rating != null) {
                        ratingService.remove(rating);
                    }
                } catch (WrongParameterException e) {
                    e.printStackTrace();
                }
                saveStateAndReload(target);
            }
        };
        played = new AjaxLink<UserPlayedGame>("played") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                selected = "Hrál jsem";
                played.add(AttributeModifier.replace("class", Model.of("active played button")));
                wantToPlay.add(AttributeModifier.replace("class", Model.of("wantToPlay button")));
                didntPlay.add(AttributeModifier.replace("class", Model.of("didnt button")));
                saveStateAndReload(target);
            }
        };
        wantToPlay = new AjaxLink<UserPlayedGame>("wantToPlay") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                selected = "Chci hrát";
                played.add(AttributeModifier.replace("class", Model.of("played button")));
                wantToPlay.add(AttributeModifier.replace("class", Model.of("active wantToPlay button")));
                didntPlay.add(AttributeModifier.replace("class", Model.of("didnt button")));
                saveStateAndReload(target);
            }
        };

        stateOfGame = userPlayedGameService.getUserPlayedGame(game.getId(), userId);
        if(stateOfGame == null) {
            stateOfGame = new UserPlayedGame();
            stateOfGame.setGameId(game.getId());
            stateOfGame.setUserId(userId);
            didntPlay.add(AttributeModifier.replace("class", Model.of("active didnt button")));
        } else {
            selected = UserPlayedGame.getStateFromDb(stateOfGame.getState());
            if(stateOfGame.getState() == 2) {
                played.add(AttributeModifier.replace("class", Model.of("active played button")));
            } else if(stateOfGame.getState() == 1) {
                wantToPlay.add(AttributeModifier.replace("class", Model.of("active wantToPlay button")));
            } else {
                didntPlay.add(AttributeModifier.replace("class", Model.of("active didnt button")));
            }
        }

        add(didntPlay);
        add(played);
        add(wantToPlay);
    }

    private void saveStateAndReload(AjaxRequestTarget target) {
        stateOfGame.setState(UserPlayedGame.getStateForDb(selected));
        stateOfGame.setPlayerOfGame(((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser());
        userPlayedGameService.saveOrUpdate(stateOfGame);

        target.add(PlayedPanel.this);
        onCsldAction(target, stateOfGame);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target, UserPlayedGame userPlayedGame){}

    public void reload(AjaxRequestTarget target) {
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        final int userId = (logged != null) ? logged.getId() : -1;

        played.add(AttributeModifier.replace("class", Model.of("played button")));
        wantToPlay.add(AttributeModifier.replace("class", Model.of("wantToPlay button")));
        didntPlay.add(AttributeModifier.replace("class", Model.of("didnt button")));

        stateOfGame = userPlayedGameService.getUserPlayedGame(game.getId(), userId);
        if(stateOfGame == null) {
            stateOfGame = new UserPlayedGame();
            stateOfGame.setGameId(game.getId());
            stateOfGame.setUserId(userId);
            didntPlay.add(AttributeModifier.replace("class", Model.of("active didnt button")));
        } else {
            selected = UserPlayedGame.getStateFromDb(stateOfGame.getState());
            if(stateOfGame.getState() == 2) {
                played.add(AttributeModifier.replace("class", Model.of("active played button")));
            } else if(stateOfGame.getState() == 1) {
                wantToPlay.add(AttributeModifier.replace("class", Model.of("active wantToPlay button")));
            } else {
                didntPlay.add(AttributeModifier.replace("class", Model.of("active didnt button")));
            }
        }

        target.add(PlayedPanel.this);
    }
}
