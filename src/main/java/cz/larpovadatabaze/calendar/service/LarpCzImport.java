package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.tags.service.DatabaseTags;
import cz.larpovadatabaze.tags.service.FilteredDatabaseTags;
import cz.larpovadatabaze.tags.service.Tags;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class allows import of the data about event from www.larp.cz/kalendar
 * It is run on the startup of application.
 */
public class LarpCzImport {
    private SessionFactory sessionFactory;
    private Tags tags;
    private Events events;

    public LarpCzImport(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        events = new LarpCzEvents();
    }

    /**
     * Goes through all events on the first page of Larp cz calendar and if they aren't already present in the database
     * it stores them.
     */
    public void importEvents(){
        Session session = sessionFactory.openSession();

        DatabaseEvents dbEvents = new DatabaseEvents(session);
        Tags tags = new FilteredDatabaseTags(new DatabaseTags(session), new FilteredDatabaseTags.Filter("LarpCz"));
        Collection<Event> imported = events.all();

        List<Label> larpCz = new ArrayList<>(tags.all());
        for(Event larpCzEvent: imported) {
            if(!dbEvents.isPersisted(larpCzEvent.getName())){
                larpCzEvent.setLabels(larpCz);
                dbEvents.store(larpCzEvent);
            }
        }

        session.close();
    }
}
