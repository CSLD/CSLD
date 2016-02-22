package cz.larpovadatabaze.calendar;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.entities.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test creation the event and storing it in the long living storage.
 */
public class GivenEventToStore extends AcceptanceTest {
    private Event event;

    @Before
    public void WhenEventIsStored(){
        Events events = new DatabaseEvents(session);

        new InTransaction(session, new Label(111), new Label(112)).store();

        Collection<Label> labelsToUse = new ArrayList<>();
        labelsToUse.add((Label) session.get(Label.class, 11));
        labelsToUse.add((Label) session.get(Label.class, 12));

        event = new Event(1, labelsToUse);

        events.store(event);
    }

    @Test
    public void ThenItIsPossibleToRetrieveEventFromSession() {
        assertThat(session.get(Event.class, 1), is(event));
    }

    @Test
    public void ThenItHasChosenLabels() {
        Event toVerify = (Event) session.get(Event.class, 1);
        assertThat(toVerify.getLabels().size(), is(2));
    }

    @After
    public void clean() {
        session.delete(session.get(Event.class, 1));
    }
}
