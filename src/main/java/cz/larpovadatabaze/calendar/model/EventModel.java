package cz.larpovadatabaze.calendar.model;

import cz.larpovadatabaze.calendar.service.Events;
import org.apache.wicket.model.LoadableDetachableModel;

abstract public class EventModel extends LoadableDetachableModel<Event> {
    // Game id. We could also store id as page property.
    private Integer eventId;

    public EventModel(Integer eventId) {
        this.eventId = eventId;
    }

    @Override
    protected Event load() {
        if (eventId == null) return Event.getEmptyEvent(); // Empty event
        return getEvents().getById(eventId);
    }

    @Override
    public void detach() {
        if (eventId != null) {
            // Detach only when not creating a new game
            super.detach();
        }
    }

    abstract public Events getEvents();
}