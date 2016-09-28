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

            boolean containsAllLabels = labels.containsAll(filterEvent.getRequiredLabels()) && labels.containsAll(filterEvent.getOtherLabels());
            boolean isInGivenTimeFrame = true;
            boolean endsBeforeLimit = true;
            boolean startsBeforeLimit = true;
            boolean startsAfterLimit = true;

            // There is some time based filter specified.
            if((filterEvent.getFrom() != null) || (filterEvent.getTo() != null)) {
                if(filterEvent.getTo() != null) {
                    endsBeforeLimit = event.getTo().before(filterEvent.getTo());
                    startsBeforeLimit = event.getFrom().before(filterEvent.getTo());
                }
                if(filterEvent.getFrom() != null) {
                    startsAfterLimit = event.getFrom().after(Calendar.getInstance());
                }

                isInGivenTimeFrame = endsBeforeLimit && startsAfterLimit && startsBeforeLimit;
            }

            if(containsAllLabels && isInGivenTimeFrame) {
                filtered.add(event);
            }
        }

        if(filterEvent.getLimit() != null && filtered.size() > filterEvent.getLimit()) {
            filtered = new ArrayList<>(filtered).subList(0, filterEvent.getLimit());
        }

        return filtered;
    }
}
