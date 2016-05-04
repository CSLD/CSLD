package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.SessionFactory;

import java.util.Iterator;
import java.util.List;

public class SortableEventProvider extends SortableDataProvider<Event, String> {
    private SessionFactory sessionFactory;

    public SortableEventProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Iterator<? extends Event> iterator(long first, long count) {
        List<Event> eventsFromTheDatabase = (List<Event>) new DatabaseEvents(sessionFactory.getCurrentSession()).all();
        int last = (int) count;
        if(first + count > eventsFromTheDatabase.size()) {
            last = eventsFromTheDatabase.size() - 1;
        }
        return eventsFromTheDatabase.subList((int)first, last).iterator();
    }

    @Override
    public long size() {
        return new DatabaseEvents(sessionFactory.getCurrentSession()).all().size();
    }

    @Override
    public IModel<Event> model(Event object) {
        return new Model(object); // TODO: Take a look, whether it doesn't create memory leak.
    }
}
