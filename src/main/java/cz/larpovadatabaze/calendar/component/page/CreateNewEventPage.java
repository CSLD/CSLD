package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.CreateEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateNewEventPage extends CsldBasePage {
    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private Environment env;

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
            DatabaseEvents eventsInDatabase = new DatabaseEvents(sessionFactory.getCurrentSession());

            List<Event> event = eventsInDatabase.all()
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

    public CreateNewEventPage(PageParameters params) {
        if(!Boolean.parseBoolean(env.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }

        if(!params.isEmpty()) {
            setDefaultModel(new EventModel(params.get("id").to(Integer.class)));
        } else {
            setDefaultModel(new EventModel(null));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CreateEventPanel("createEvent", (EventModel)getDefaultModel()) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                CsldAuthenticatedWebSession session = CsldAuthenticatedWebSession.get();
                session.requestClear();

                throw new RestartResponseException(ListEventsPage.class);
            }
        });
    }
}
