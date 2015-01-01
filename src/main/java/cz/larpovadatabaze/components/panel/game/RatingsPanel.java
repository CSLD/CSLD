package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;

/**
 * This panel contains Slider which allows user to rate game. It also has info about rating the game, if the game was
 * not rated by the user previously.
 */
public class RatingsPanel extends Panel {
    private static final int NUM_STARS = 10;
    @SpringBean
    RatingService ratingService;

    private final Component refreshComponent;
    private final int gameId;
    private final IModel<Rating> model;
    private final IModel<Game> gameModel;

    /**
     * Model for rating of a game, implemented as loaded and detachable
     */
    private class RatingModel extends LoadableDetachableModel<Rating> {
        @Override
        protected Rating load() {
            CsldUser logged = getLoggedUser();
            int loggedId = getLoggedUserId();

            Rating actualRating = null;
            try {
                actualRating = ratingService.getUserRatingOfGame(loggedId, gameId);
            } catch (WrongParameterException e) {
                // This should never happen.
                e.printStackTrace();
            }

            if(actualRating != null){
                if(actualRating.getUser() == null){
                    actualRating.setUser(logged);
                    actualRating.setUserId(loggedId);
                }
            } else {
                actualRating = new Rating();
                actualRating.setGameId(gameId);
                actualRating.setUser(logged);
                actualRating.setUserId(loggedId);
            }

            return actualRating;
        }
    }

    /**
     * Model returning "active" if this star should be active. Star number is passed as a parameter to the constructor
     */
    private class ActiveStarClassModel extends AbstractReadOnlyModel<String> {

        private final int starNo;

        private ActiveStarClassModel(int starNo) {
            this.starNo = starNo;
        }

        @Override
        public String getObject() {
            Integer rating = model.getObject().getRating();
            if (rating == null) return ""; // Not rated

            return (starNo <= rating)?"active":"";
        }
    }

    public RatingsPanel(String id, IModel<Game> gameModel, Component refreshComponent) {
        super(id);
        this.gameId = gameModel.getObject().getId();
        this.gameModel = gameModel;
        this.model = new RatingModel();
        this.refreshComponent = refreshComponent;
    }

    private CsldUser getLoggedUser() {
        return CsldAuthenticatedWebSession.get().getLoggedUser();
    }

    private int getLoggedUserId() {
        CsldUser logged = getLoggedUser();
        return (logged != null) ? logged.getId() : -1;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add star labels
        for(int i=1; i<=NUM_STARS; i++) {
            StarLabel label = new StarLabel("star"+i,i);
            label.add(new AttributeAppender("class", new ActiveStarClassModel(i), " "));
            add(label);
        }

        setOutputMarkupId(true);
    }

    private void updateData(int value){
        // Set and save new value
        model.getObject().setRating(value);
        ratingService.saveOrUpdate(model.getObject());

        // Flush game so that computed fields are reloaded
        gameModel.detach();
    }

    public boolean isRatingSet() {
        return (model.getObject().getRating() != null);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        model.detach(); // Refresh model data
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn() && !gameModel.getObject().isRatingsDisabled());
    }

    private class StarLabel extends Label{
        private final int value;

        public StarLabel(String id, int pValue) {
            super(id);

            this.value = pValue;
            add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    updateData(value);

                    target.add(refreshComponent); // Just refresh all the ratings
                }
            });

            setOutputMarkupId(true);
        }
    }
}
