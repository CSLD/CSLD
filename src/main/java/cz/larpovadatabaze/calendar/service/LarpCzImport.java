package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class allows import of the data about event from www.larp.cz/kalendar
 * It is run on the startup of application.
 */
public class LarpCzImport {
    private Events dbEvents;
    private TokenSearch tokenSearch;
    private LarpCzEvents events;
    private SessionFactory sessionFactory;
    private GoogleCalendarEvents googleCalendarEvents;

    public LarpCzImport(Events dbEvents, LarpCzEvents larpEvents, SessionFactory sessionFactory, TokenSearch tokenSearch, GoogleCalendarEvents googleCalendarEvents) {
        this.dbEvents = dbEvents;
        this.events = larpEvents;
        this.sessionFactory = sessionFactory;
        this.tokenSearch = tokenSearch;
        this.googleCalendarEvents = googleCalendarEvents;
    }

    /**
     * Goes through all events on the first page of Larp cz calendar and if they aren't already present in the database
     * it stores them.
     */
    public void importEvents() {
        try (Session session = sessionFactory.openSession()) {
            Transaction eventsImport = session.beginTransaction();

            Collection<Event> imported = events.all();

            List<Label> larpCz = new ArrayList<>(tokenSearch.findLabels("LarpCz"));

            for (Event larpCzEvent : imported) {
                if (dbEvents.byName(larpCzEvent.getName()).size() == 0) {
                    larpCzEvent.setLabels(larpCz);
                    dbEvents.saveOrUpdate(larpCzEvent);
                    googleCalendarEvents.createEvent(larpCzEvent);
                }
            }

            eventsImport.commit();
        }
    }
}
