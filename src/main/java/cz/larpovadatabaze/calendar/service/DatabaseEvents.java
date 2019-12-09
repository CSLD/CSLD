package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.entities.CsldUser;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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

        Event toSave = event;
        if(!session.contains(toSave)) {
            toSave = (Event) session.merge(toSave);
        }
        if(toSave.getAddedBy() != null && !session.contains(toSave.getAddedBy())) {
            toSave.setAddedBy((CsldUser) session.merge(toSave.getAddedBy()));
        }
        session.persist(toSave);
        session.flush();

        event.setId(toSave.getId());
    }

    @Override
    public void delete(Event event) {
        session.delete(event);
    }

    @Override
    public Collection<Event> all() {
        return session.createCriteria(Event.class).list();
    }

    boolean isPersisted(String name) {
        return session.createCriteria(Event.class).add(Restrictions.eq("name", name)).list().size() > 0;
    }
}
