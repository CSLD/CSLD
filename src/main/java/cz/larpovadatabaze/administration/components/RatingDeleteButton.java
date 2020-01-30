package cz.larpovadatabaze.administration.components;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Jakub Balhar
 * Date: 4/28/14
 * Time: 10:08 PM
 */
public class RatingDeleteButton extends AbstractCsldPanel<Rating> {
    @SpringBean
    private Ratings ratings;
    @SpringBean
    private AppUsers appUsers;

    private IModel<Game> gameModel;

    public RatingDeleteButton(String id, IModel<Rating> model, IModel<Game> gameModel) {
        super(id, model);
        this.gameModel = gameModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Determine if we are visible
        if (!appUsers.isEditor()) {
            // Not visible - do not show at all
            setVisible(false);
            return;
        }

        AjaxLink<Game> button = new AjaxLink<>("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Rating rating = RatingDeleteButton.this.getModelObject();

                ratings.remove(rating);

                gameModel.detach();
            }
        };
        button.setOutputMarkupId(true);

        add(button);
    }
}
