package cz.larpovadatabaze.calendar;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * It must be possible to delete event.
 */
public class GivenEventToDelete extends AcceptanceTest{
    @Before
    public void WhenEventIsDeleted(){
        DatabaseEvents events = new DatabaseEvents(session);

        Event toDelete = new Event("1");
        session.persist(toDelete);

        events.delete(toDelete);
    }

    @Test
    public void ThenItIsntPossibleToRetrieveItFromSession(){
        assertThat(session.get(Event.class, 1), is(nullValue()));
    }
}
