package cz.larpovadatabaze.calendar.component.page;

import cz.larpovadatabaze.api.Toggles;
import cz.larpovadatabaze.calendar.component.panel.DeleteEventPanel;
import cz.larpovadatabaze.calendar.component.panel.DetailedEventPanel;
import cz.larpovadatabaze.calendar.component.panel.EditEventPanel;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.LarpCzEvents;
import cz.larpovadatabaze.calendar.service.ReadOnlyEvents;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.utils.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.core.env.Environment;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.GMapHeaderContributor;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.event.ClickListener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * It shows detailed information about one event.
 */
public class DetailOfEventPage extends CsldBasePage {
    private static final String ID_PARAM = "id";

    @SpringBean
    private Environment environment;
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

    public DetailOfEventPage(PageParameters params) {
        if(!Boolean.parseBoolean(environment.getProperty(Toggles.CALENDAR)) &&
                !CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            throw new RestartResponseException(HomePage.class);
        }

        if(params.isEmpty()) {
            throw new RestartResponseException(ListEventsPage.class);
        }

        EventModel model = new EventModel(params.get("id").toInt());
        setDefaultModel(model);

        if(model.getObject().isDeleted()) {
            throw new RestartResponseException(ListEventsPage.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new DetailedEventPanel("eventDetail", (IModel<Event>) getDefaultModel()));

        add(new EditEventPanel("editEvent", (IModel<Event>) getDefaultModel()));
        add(new DeleteEventPanel("deleteEvent", (IModel<Event>) getDefaultModel()));

        GMap map = new GMap("map", new GMapHeaderContributor("http","AIzaSyC8K3jrJMl52-Mswi2BsS5UVKDZIT4GWh8")); // TODO: Restrict usage of the key and move to config.
        map.setStreetViewControlEnabled(false);
        map.setScaleControlEnabled(true);
        map.setScrollWheelZoomEnabled(true);

        Event event = (Event)getDefaultModelObject();
        if(event.getLocation() != null) {
            GLatLng location = new GLatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
            map.setCenter(location);
            map.addOverlay(new GMarker(new GMarkerOptions(map, location)));
        } else {
            map.setVisibilityAllowed(false);
        }

        add(map);

        add(new GameListPanel("associatedGames", new LoadableDetachableModel<List<? extends Game>>() {
            @Override
            protected List<Game> load() {
                return ((Event) getDefaultModel().getObject()).getGames();
            }
        }));
    }

    public static PageParameters pageParameters(Event event) {
        PageParameters pp = new PageParameters();

        if (event != null) {
            pp.add(ID_PARAM, event.getId());
            pp.add("name", Strings.removeAccents(event.getName()).toLowerCase().replaceAll("[^a-z0-9\\.]", "-").replaceAll("-+", "-").replaceAll("-$", ""));
        }

        return pp;
    }
}
