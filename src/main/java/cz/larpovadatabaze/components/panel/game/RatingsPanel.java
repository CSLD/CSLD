package cz.larpovadatabaze.components.panel.game;

import com.googlecode.wicket.jquery.ui.form.slider.AjaxSlider;
import com.googlecode.wicket.jquery.ui.form.slider.Slider;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * This panel contains Slider which allows user to rate game. It also has info about rating the game, if the game was
 * not rated by the user previously.
 */
public class RatingsPanel extends Panel {
    @SpringBean
    RatingService ratingService;

    private Rating actualRating;

    public RatingsPanel(String id, Integer gameId) {
        super(id);

        Form form = new Form("ratingForm");

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        // Be Careful logged can be null. It is valid value for it.
        int loggedId = (logged != null) ? logged.getId() : -1;
        Integer rating = 0;

        final Model<Integer> ratingOfGame = new Model<Integer>(rating);
        final Label label = new Label("rating", ratingOfGame);
        form.add(label);
        label.setOutputMarkupId(true);

        try {
            actualRating = ratingService.getUserRatingOfGame(loggedId, gameId);
        } catch (WrongParameterException e) {
            // This should never happen.
            e.printStackTrace();
        }
        if(actualRating != null){
            ratingOfGame.setObject(actualRating.getRating());
        } else {
            actualRating = new Rating();
            actualRating.setGameId(gameId);
            actualRating.setUserId(loggedId);
        }


        AjaxSlider slider = new AjaxSlider("slider", ratingOfGame, label) {
            @Override
            public void onValueChanged(AjaxRequestTarget target)
            {
                saveOrUpdateRating(ratingOfGame);
            }
        };
        slider.setRange(Slider.Range.MIN).setMax(10);
        slider.setRange(Slider.Range.MAX).setMin(1);
        form.add(slider);

        add(form);
    }

    private void saveOrUpdateRating(Model<Integer> ratingOfGame) {
        // TODO show info about saving.

        Integer rating = ratingOfGame.getObject();
        actualRating.setRating(rating);
        ratingService.saveOrUpdate(actualRating);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
