package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.Ratings;
import cz.larpovadatabaze.utils.UserUtils;
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

    private IModel<Game> gameModel;

    public RatingDeleteButton(String id, IModel<Rating> model, IModel<Game> gameModel) {
        super(id, model);
        this.gameModel = gameModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Determine if we are visible
        if (!UserUtils.isEditor()) {
            // Not visible - do not show at all
            setVisible(false);
            return;
        }

        AjaxLink button = new AjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Rating rating = RatingDeleteButton.this.getModelObject();

                ratings.delete(rating);

                gameModel.detach();
            }
        };
        button.setOutputMarkupId(true);

        add(button);
    }
}
