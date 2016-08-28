package cz.larpovadatabaze.calendar.service;


import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterEvent;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class FilteredReadOnlyEvents implements Events {
    private Events eventsToFilter;
    private IModel<FilterEvent> filterCriteria;

    public FilteredReadOnlyEvents(IModel<FilterEvent> filter, Events events) {
        eventsToFilter = events;
        filterCriteria = filter;
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
        Collection<Event> events = eventsToFilter.all();
        Collection<Event> filtered = new ArrayList<>();

        FilterEvent filterEvent = filterCriteria.getObject();
        for(Event event: events) {
            if(event.isDeleted()) {
                continue;
            }

            List<Label> labels = event.getLabels();
            if(labels.containsAll(filterEvent.getRequiredLabels()) && labels.containsAll(filterEvent.getOtherLabels())) {
                if(filterEvent.isShowOnlyFuture()) {
                    if(event.getFrom().after(Calendar.getInstance())) {
                        filtered.add(event);
                    }
                } else {
                    filtered.add(event);
                }
            }
        }

        return filtered;
    }
}
