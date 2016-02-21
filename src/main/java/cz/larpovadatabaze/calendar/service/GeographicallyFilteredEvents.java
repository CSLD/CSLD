package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.model.Event;

import java.util.Collection;
import java.util.stream.Collectors;

public class GeographicallyFilteredEvents implements Events {
    private Events events;
    private BoundingBox limitation;

    public GeographicallyFilteredEvents(Events events, BoundingBox limitation) {
        this.events = events;
        this.limitation = limitation;
    }

    @Override
    public void store(Event event) {
        events.store(event);
    }

    @Override
    public void delete(Event event) {
        events.delete(event);
    }

    @Override
    public Collection<Event> all() {
        Collection<Event> loaded = events.all();
        return loaded
                .stream()
                .filter(event -> limitation.isInArea(event.getLocation()))
                .collect(Collectors.toList());
    }
}
