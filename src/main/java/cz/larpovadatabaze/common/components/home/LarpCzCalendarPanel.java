package cz.larpovadatabaze.common.components.home;

import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.components.BookmarkableLinkWithLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Calendar;
import java.util.List;

/**
 * Panel for upcoming events from larp.cz
 *
 * User: Michal Kara Date: 7.3.15 Time: 21:19
 */
public class LarpCzCalendarPanel extends Panel {
    @SpringBean
    private Events events;

    public LarpCzCalendarPanel(String id) {
        super(id);
    }

    private class EventsModel extends LoadableDetachableModel<List<Event>> {
        @Override
        protected List<Event> load() {
            FilterEvent filter = new FilterEvent();
            filter.setFrom(Calendar.getInstance().getTime());
            filter.setLimit(6);
            filter.setSorted(FilterEvent.Sort.TIME_MOST_RECENT);

            return events.filtered(new Model<>(filter));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ListView<Event> events = new ListView<Event>("events", new EventsModel()) {
            @Override
            protected void populateItem(ListItem<Event> item) {
                Event event = item.getModelObject();

                item.add(
                        new BookmarkableLinkWithLabel(
                                "link",
                                DetailOfEventPage.class,
                                Model.of(event.getName()),
                                Model.of(DetailOfEventPage.pageParameters(event))
                        )
                );

                item.add(new Label("date", event.getFromCzech() + " - " + event.getToCzech()));
                item.add(new Label("players", event.getAmountOfPlayers()));
                item.add(new Label("place", event.getLoc()));
            }
        };

        add(events);
    }
}
