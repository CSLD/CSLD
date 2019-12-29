package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.entities.Game;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Game rating box which also includes average rating
 *
 * User: Michal Kara Date: 6.4.15 Time: 17:40
 */
public class GameRatingBoxWithAveragePanel extends GameRatingBoxPanel {
    public GameRatingBoxWithAveragePanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected Double getMainRating() {
        return getModelObject().getTotalRating();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game g = getModelObject();
        if (g.getTotalRating() == 0) {
            // Do not show
            wrapper.add(new Label("averageRating", "").setVisible(false));
        }
        else {
            // Show average rating
            wrapper.add(new Label("averageRating", format.format(g.getAverageRating()/10d)));
        }
    }
}
