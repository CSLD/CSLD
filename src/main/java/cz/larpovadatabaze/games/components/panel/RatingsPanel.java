package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This panel contains Slider which allows user to rate game. It also has info about rating the game, if the game was
 * not rated by the user previously.
 */
public class RatingsPanel extends Panel {
    private static final int NUM_STARS = 10;
    @SpringBean
    Ratings ratings;

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

            Rating actualRating = ratings.getUserRatingOfGame(loggedId, gameId);

            if(actualRating != null){
                if(actualRating.getUser() == null){
                    actualRating.setUser(logged);
                }
            } else {
                actualRating = new Rating();
                actualRating.setGame(gameModel.getObject());
                actualRating.setUser(logged);
            }

            return actualRating;
        }
    }

    /**
     * Model returning "active" if this star should be active. Star number is passed as a parameter to the constructor
     */
    private class ActiveStarClassModel implements IModel<String> {

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
        ratings.saveOrUpdate(model.getObject());

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