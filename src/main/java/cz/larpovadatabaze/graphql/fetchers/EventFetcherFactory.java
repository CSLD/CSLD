package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.calendar.service.Events;
import graphql.schema.DataFetcher;
import org.apache.wicket.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class EventFetcherFactory {
    private final Events events;

    @Autowired
    public EventFetcherFactory(Events events) {
        this.events = events;
    }

    public DataFetcher<List<Event>> createNextEventsFetcher() {
        return dataFetchingEnvironment -> {
            FilterEvent filter = new FilterEvent();
            filter.setFrom(Calendar.getInstance().getTime());
            filter.setLimit(6);
            filter.setSorted(FilterEvent.Sort.TIME_MOST_RECENT);

            return events.filtered(Model.of(filter));
        };
    }
}
