package cz.larpovadatabaze.calendar.GivenThereAreEventsInTheDatabase;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.InTransaction;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class All extends AcceptanceTest {
    private Collection<Event> loaded;

    @Before
    public void WhenAllEventsAreRequested(){
        Events events = new DatabaseEvents(session);

        new InTransaction(session, new Event(1), new Event(2), new Event(3)).store();

        loaded = events.all();
    }

    @Test
    public void ThenAllEventsAreRetrieved() {
        assertThat(loaded.size(), is(3));
    }
}
