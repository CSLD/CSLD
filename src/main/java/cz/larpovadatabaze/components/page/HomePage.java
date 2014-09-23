package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.LastGamesPanel;
import cz.larpovadatabaze.components.panel.home.RandomLarpPanel;
import cz.larpovadatabaze.components.panel.home.StatisticsPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.wicketstuff.simile.timeline.Timeline;
import org.wicketstuff.simile.timeline.TimelineEventModel;
import org.wicketstuff.simile.timeline.TimelineModel;

/**
 *
 */
public class HomePage extends CsldBasePage {
    public HomePage(){
        IModel<TimelineModel> timelineModel = new IModel<TimelineModel>() {
            TimelineModel timeline;

            @Override
            public TimelineModel getObject() {
                return timeline;
            }

            @Override
            public void setObject(TimelineModel object) {
                this.timeline = object;
            }

            @Override
            public void detach() {}
        };
        TimelineModel timeline = new TimelineModel();
        // timeline.addEvent(new TimelineEventModel());
        timelineModel.setObject(timeline);

        add(new Timeline("timeline", timelineModel));
        add(new LastGamesPanel("lastGames"));
        add(new LastCommentsPanel("lastComments"));

        add(new AddGamePanel("createGamePanel"));
        add(new RandomLarpPanel("randomLarpPanel"));
        add(new StatisticsPanel("statisticsPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }
}
