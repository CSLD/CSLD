package cz.larpovadatabaze.calendar.GivenThereAreEventsInTheDatabase;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.calendar.InTransaction;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.EventsInTimeFrame;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FilteredByTime extends AcceptanceTest {
    private Collection<Event> loaded;

    @Before
    public void WhenUserRequestsEventsInGivenTimeFrame() {

        Event beforeTimeFrame = new Event("1", calendarForDate(2014, Calendar.OCTOBER, 12), calendarForDate(2014, Calendar.OCTOBER, 14));
        Event afterTimeFrame = new Event("2", calendarForDate(2016, Calendar.OCTOBER, 1), calendarForDate(2016, Calendar.OCTOBER, 3));
        Event fullyInTimeFrame = new Event("3", calendarForDate(2015, Calendar.NOVEMBER, 5), calendarForDate(2015, Calendar.NOVEMBER, 7));


        new InTransaction(session, beforeTimeFrame, afterTimeFrame, fullyInTimeFrame).store();

        Calendar from = calendarForDate(2015, Calendar.SEPTEMBER, 4);
        Calendar to = calendarForDate(2016, Calendar.SEPTEMBER, 4);

        Events temporallyFiltered = new EventsInTimeFrame(new DatabaseEvents(session), from, to);

        loaded = temporallyFiltered.all();
    }

    @Test
    public void ThenOnlyEventsInGivenTimeFrameAreReturned() {
        assertThat(loaded.size(), is(1));
    }

    private Calendar calendarForDate(int year, int month, int day) {
        Calendar representDate = Calendar.getInstance();
        representDate.set(year, month, day);
        return representDate;
    }
}
