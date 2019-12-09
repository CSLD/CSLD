package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.ReadOnlyEvents;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

abstract public class AbstractListEventPanel<T> extends AbstractCsldPanel<T> {
    private SortableDataProvider<Event, String> sdp;
    @SpringBean
    private SessionFactory sessionFactory;

    /**
     * Model for event specified by event id
     */
    private class EventModel extends LoadableDetachableModel<Event> {

        // Game id. We could also store id as page property.
        private Integer eventId;

        private EventModel(Integer eventId) {
            this.eventId = eventId;
        }

        @Override
        protected Event load() {
            if (eventId == null) return Event.getEmptyEvent(); // Empty event
            ReadOnlyEvents allEvents = new ReadOnlyEvents(
                    new DatabaseEvents(sessionFactory.getCurrentSession())
            );

            List<Event> event = allEvents.all()
                    .stream()
                    .filter(event1 -> Objects.equals(event1.getId(), eventId))
                    .collect(Collectors.toList());

            return event.get(0);
        }

        @Override
        public void detach() {
            if (eventId != null) {
                // Detach only when not creating a new game
                super.detach();
            }
        }
    }

    public AbstractListEventPanel(String id) {
        super(id);
    }

    protected abstract SortableDataProvider<Event, String> getDataProvider();

    @Override
    protected void onInitialize() {
        super.onInitialize();

        sdp = getDataProvider();
        final DataView<Event> propertyList = new DataView<Event>("listEvents", sdp) {
            @Override
            protected void populateItem(Item<Event> item) {
                Event event = item.getModelObject();

                item.add(new EventNameAndLabelsPanel("nameAndLabels", new EventModel(event.getId())));
                item.add(new Label("loc", Model.of(event.getLoc())));
                item.add(new Label("date", event.getDate()));
                item.add(new ExternalLink("web", Model.of(event.getWeb()), Model.of(event.getWeb())).setVisible(StringUtils.isNotBlank(event.getWeb())));
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }
}
