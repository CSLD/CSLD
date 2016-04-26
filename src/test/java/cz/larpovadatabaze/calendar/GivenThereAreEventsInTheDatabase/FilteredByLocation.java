package cz.larpovadatabaze.calendar.GivenThereAreEventsInTheDatabase;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.InTransaction;
import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.GeographicallyFilteredEvents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FilteredByLocation extends AcceptanceTest{
    Collection<Event> loaded;

    @Before
    public void WhenEventsInAreaAreRequested(){
        Events events = new GeographicallyFilteredEvents(
                new DatabaseEvents(session),
                new BoundingBox(new Location(50d, 10d), new Location(55d, 25d))
        );

        new InTransaction(session,
                new Event("1", new Location(51d, 14d)),
                new Event("2", new Location(52d, 20d)),
                new Event("3", new Location(13d, 4d))
            ).store();

        loaded = events.all();
    }

    @Test
    public void ThenOnlyEventsInAreaAreReturned() {
        assertThat(loaded.size(), is(2));
    }
}
