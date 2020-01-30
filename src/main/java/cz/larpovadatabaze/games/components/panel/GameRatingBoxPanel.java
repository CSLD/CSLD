package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Ratings;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;

/**
 * Panel to show game rating. Centralized so that various traits are consistent across application.
 * <p>
 * User: Michal Kara Date: 6.4.15 Time: 17:20
 */
public class GameRatingBoxPanel extends AbstractCsldPanel<Game> {
    @SpringBean
    private Ratings ratings;

    /* Format of the score */
    protected final static DecimalFormat format = new DecimalFormat("0.0");

    protected WebMarkupContainer wrapper;

    public GameRatingBoxPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    protected Double getMainRating() {
        return getModelObject().getAverageRating();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game g = getModelObject();

        // Wrapper && wrapper
        wrapper = new WebMarkupContainer("wrapper");
        add(wrapper);
        wrapper.add(new AttributeAppender("class", Model.of(ratings.getColor(g.getTotalRating())), " "));

        // Rating
        if (g.getTotalRating() == 0) {
            // No rating
            wrapper.add(new Label("rating", "–"));
        }
        else {
            // Numeric rating
            wrapper.add(new Label("rating", format.format(getMainRating()/10d)));
        }
    }
}
