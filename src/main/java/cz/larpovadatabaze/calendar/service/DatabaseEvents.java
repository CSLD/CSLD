package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import org.hibernate.Session;

import java.util.Collection;

/**
 * Use Database as a persistent storage.
 */
public class DatabaseEvents implements Events {
    private Session session;

    public DatabaseEvents(Session session){
        this.session = session;
    }

    @Override
    public void store(Event event) {
        // Prepares the event to the state, which is safe for the application.
        event.sanitize();
        session.persist(event);
        session.flush();  // TODO: Understand why the persist doesn't work. Probably unfinished transaction.
    }

    @Override
    public void delete(Event event) {
        session.delete(event);
    }

    @Override
    public Collection<Event> all() {
        return session.createCriteria(Event.class).list();
    }
}
