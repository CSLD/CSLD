package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;

import java.util.Calendar;
import java.util.Collection;
import java.util.stream.Collectors;

public class EventsInTimeFrame implements Events {
    private Events events;
    private Calendar from;
    private Calendar to;

    public EventsInTimeFrame(Events events, Calendar from, Calendar to) {
        this.events = events;
        this.from = from;
        this.to = to;
    }


    @Override
    public void store(Event event) {
        events.store(event);
    }

    @Override
    public void delete(Event event) {
        events.delete(event);
    }

    @Override
    public Collection<Event> all() {
        Collection<Event> all = events.all();
        return all.
                stream().
                filter(this::isInTimeFrame)
                .collect(Collectors.toList());
    }

    private boolean isInTimeFrame(Event event) {
        return fullyInTimeFrame(event) ||
                partiallyInTimeFrame(event) ||
                enclosingTheTimeFrame(event);
    }

    private boolean enclosingTheTimeFrame(Event event) {
        return event.getFrom().before(from) && event.getTo().after(to);
    }

    private boolean partiallyInTimeFrame(Event event) {
        return startingBeforeEndingIn(event) ||
                startingInEndingAfter(event);
    }

    private boolean startingBeforeEndingIn(Event event) {
        return event.getFrom().before(from) && event.getTo().before(to) && event.getTo().after(from);
    }

    private boolean startingInEndingAfter(Event event) {
        return event.getFrom().after(from) && event.getFrom().before(to) && event.getTo().after(to);
    }

    private boolean fullyInTimeFrame(Event event) {
        return event.getFrom().after(from) && event.getTo().before(to);
    }
}
