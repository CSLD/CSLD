package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.DetailedEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.LarpCzEvents;
import cz.larpovadatabaze.calendar.service.ReadOnlyEvents;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * It shows detailed information about one event.
 */
public class DetailOfEventPage extends CsldBasePage {
    @SpringBean
    private Environment environment;
    @SpringBean
    private SessionFactory sessionFactory;

    public static PageParameters pageParameters(Event event) {
        PageParameters result = new PageParameters();
        result.add("name", event.getName());
        result.add("id", event.getId());
        return result;
    }

    /**
     * Model for event specified by event id
     */
    private class EventModel extends LoadableDetachableModel<Event> {

        // Game id. We could also store id as page property.
        private String eventId;

        private EventModel(String eventId) {
            this.eventId = eventId;
        }

        @Override
        protected Event load() {
            if (eventId == null) return Event.getEmptyEvent(); // Empty event
            ReadOnlyEvents allEvents = new ReadOnlyEvents(
                    new DatabaseEvents(sessionFactory.getCurrentSession()),
                    new LarpCzEvents()
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

    public DetailOfEventPage(PageParameters params) {
        if(!Boolean.parseBoolean(environment.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }

        if(params.isEmpty()) {
            throw new RestartResponseException(ListEventsPage.class);
        }

        setDefaultModel(new EventModel(params.get("id").toString()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new DetailedEventPanel("eventDetail", getDefaultModel()));
    }
}
