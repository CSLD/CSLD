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
        session.persist(event);
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
