package cz.larpovadatabaze.calendar.model;

import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.ReadOnlyEvents;
import org.apache.wicket.model.LoadableDetachableModel;
import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

abstract public class EventModel extends LoadableDetachableModel<Event> {
    // Game id. We could also store id as page property.
    private Integer eventId;

    public EventModel(Integer eventId) {
        this.eventId = eventId;
    }

    @Override
    protected Event load() {
        if (eventId == null) return Event.getEmptyEvent(); // Empty event
        ReadOnlyEvents allEvents = new ReadOnlyEvents(
                new DatabaseEvents(getSession())
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

    abstract public Session getSession();
}