package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Displays all events as simple links.
 */
public class EventsListLinksOnly extends Panel {
    public EventsListLinksOnly(String id, IModel<List<Event>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ListView<Event> listView = new ListView<Event>("listEvents", (List<Event>) getDefaultModelObject()) {
            @Override
            protected void populateItem(ListItem item) {
                Event event = (Event) item.getModelObject();
                String time = event.getFromCzech() + " - " + event.getToCzech(); // TODO: Move to the event.

                final BookmarkablePageLink<CsldBasePage> gameDetail =
                        new BookmarkablePageLink<>("eventDetail", DetailOfEventPage.class, DetailOfEventPage.pageParameters(event));
                final Label gameName = new Label("eventName", event.getName());
                final Label gameYear = new Label("eventTime", time);
                gameDetail.add(gameName);
                gameDetail.add(gameYear);
                item.add(gameDetail);
            }
        };
        add(listView);

    }
}
