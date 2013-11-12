package cz.larpovadatabaze.components.panel.game;

import com.googlecode.wicket.jquery.ui.form.slider.AjaxSlider;
import com.googlecode.wicket.jquery.ui.form.slider.Slider;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This panel contains Slider which allows user to rate game. It also has info about rating the game, if the game was
 * not rated by the user previously.
 */
public abstract class RatingsPanel extends Panel {
    @SpringBean
    RatingService ratingService;

    private Rating actualRating;
    public RatingsPanel(String id, final Game game) {
        super(id);

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        // Be Careful logged can be null. It is valid value for it.
        int loggedId = (logged != null) ? logged.getId() : -1;

        actualRating = null;
        try {
            actualRating = ratingService.getUserRatingOfGame(loggedId, game.getId());
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
            actualRating.setGame(game);
            actualRating.setGameId(game.getId());
            actualRating.setUser(logged);
            actualRating.setUserId(loggedId);
        }

        add(new StarLabel("star1",1));
        add(new StarLabel("star2",2));
        add(new StarLabel("star3",3));
        add(new StarLabel("star4",4));
        add(new StarLabel("star5",5));
        add(new StarLabel("star6",6));
        add(new StarLabel("star7",7));
        add(new StarLabel("star8",8));
        add(new StarLabel("star9",9));
        add(new StarLabel("star10",10));

        setActive();
        setOutputMarkupId(true);
    }

    private void saveAndShow(int value){
        actualRating.setRating(value);
        ratingService.saveOrUpdate(actualRating);

        setActive();
    }

    private void setActive() {
        int rating = actualRating.getRating()!= null ? actualRating.getRating() : 0;
        for(int i =1; i <= rating; i++){
            get("star" + i).add(AttributeModifier.replace("class",Model.of("active icon-star")));
        }
        for(int i=rating+1; i <= 10; i++){
            get("star" + i).add(AttributeModifier.replace("class",Model.of("icon-star")));
        }
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target){}

    private class StarLabel extends Label{
        private int value;

        public StarLabel(String id, int pValue) {
            super(id);

            this.value = pValue;
            add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    saveAndShow(value);

                    onCsldAction(target);
                    target.add(getParent());
                }
            });

            setOutputMarkupId(true);
        }
    }
}
