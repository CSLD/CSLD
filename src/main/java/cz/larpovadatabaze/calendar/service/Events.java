package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;

import java.util.Collection;

/**
 * Service for manipulating events.
 */
public interface Events {
    /**
     * Store Event into persistent store, for example database.
     * @param event Event to store into persistent store.
     */
    void store(Event event);

    /**
     * Remove instance with given id if present. If it isn't present it shouldn't do anything.
     * @param event Event to be removed from persistent storage.
     */
    void delete(Event event);

    /**
     * It returns all events in current store.
     * @return Events stored in current store.
     */
    Collection<Event> all();
}
