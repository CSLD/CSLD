package cz.larpovadatabaze.calendar;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test creation the event and storing it in the long living storage.
 */
public class GivenEventToStore extends AcceptanceTest {
    private Event event;

    @Before
    public void WhenEventIsStored(){
        DatabaseEvents events = new DatabaseEvents(session);
        event = new Event();

        events.store(event);
    }

    @Test
    public void ThenItIsPossibleToRetrieveEventFromSession() {
        assertThat(session.get(Event.class, 1), is(event));
    }

    @After
    public void clean() {
        session.delete(session.get(Event.class, 1));
    }
}
