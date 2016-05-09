package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Combines all sources for retrieval of ReadOnly messages.
 */
public class ReadOnlyEvents implements Events{
    private Events[] sources;

    public ReadOnlyEvents(Events... sourcesOfEvents) {
        this.sources = sourcesOfEvents;
    }

    @Override
    public void store(Event event) {
        throw new UnsupportedOperationException("It isn't possible to store events when readonly");
    }

    @Override
    public void delete(Event event) {
        throw new UnsupportedOperationException("It isn't possible to delete events when readonly");
    }

    @Override
    public Collection<Event> all() {
        Collection<Event> results = new ArrayList<>();
        for(Events source: sources) {
            results.addAll(source.all());
        }
        return results;
    }
}
