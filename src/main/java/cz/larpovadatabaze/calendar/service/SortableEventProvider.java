package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.models.FilterEvent;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SortableEventProvider extends SortableDataProvider<Event, String> {
    private Events events;

    private IModel<FilterEvent> filterModel;

    public SortableEventProvider(Events events, IModel<FilterEvent> filterModel) {
        this.events = events;
        this.filterModel = filterModel;
    }

    @Override
    public Iterator<? extends Event> iterator(long first, long count) {
        List<Event> eventsFromTheDatabase = (List<Event>) getEvents();
        int last = (int) count + (int) first;
        if (first + count > eventsFromTheDatabase.size()) {
            last = eventsFromTheDatabase.size() - 1;
        }
        return eventsFromTheDatabase.subList((int)first, last).iterator();
    }

    @Override
    public long size() {
        return getEvents().size();
    }

    @Override
    public IModel<Event> model(Event object) {
        return new Model(object); // TODO: Take a look, whether it doesn't create memory leak.
    }

    private Collection<Event> getEvents() {
        return events.filtered(filterModel);
    }
}
