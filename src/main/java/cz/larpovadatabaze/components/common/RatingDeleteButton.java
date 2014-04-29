package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.components.page.game.ListGame;
import cz.larpovadatabaze.components.panel.admin.AdminAllRatingsPanel;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.RestartResponseException;
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
    private RatingService ratingService;

    public RatingDeleteButton(String id, IModel<Rating> model) {
        super(id, model);
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

                ratingService.delete(rating);

                throw new RestartResponseException(ListGame.class);
            }
        };
        button.setOutputMarkupId(true);

        add(button);
    }
}
