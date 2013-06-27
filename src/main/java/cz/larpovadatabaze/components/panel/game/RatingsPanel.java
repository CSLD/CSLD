package cz.larpovadatabaze.components.panel.game;

import com.googlecode.wicket.jquery.ui.form.slider.Slider;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 13.5.13
 * Time: 22:32
 */
public class RatingsPanel extends Panel {
    public RatingsPanel(String id, Integer gameId) {
        super(id);

        Form form = new Form("ratingForm");

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        Integer rating = logged.getRatingOfGame(gameId).getRating();
        if(rating == null){
            rating = 5;
        }

        final Model<Integer> model = new Model<Integer>(rating);
        final Label label = new Label("rating", model);
        form.add(label);

        Slider slider = new Slider("slider", model, label);
        slider.setRange(Slider.Range.MIN).setMax(10);
        form.add(slider);

        AjaxButton submit = new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                //TODO solve submitting of this form. (Save Rating into database)
            }
        };
        form.add(submit);
        add(form);
    }
}
