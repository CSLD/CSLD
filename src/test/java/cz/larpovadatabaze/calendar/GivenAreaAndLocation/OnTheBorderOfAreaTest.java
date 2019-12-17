package cz.larpovadatabaze.calendar.GivenAreaAndLocation;

import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.Location;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnTheBorderOfAreaTest {
    private BoundingBox area;
    private Location location;

    @Before
    public void WhenTheLocationIsOnTheBorderOfTheArea() {
        area = new BoundingBox(new Location(10d, 10d), new Location(20d, 20d));
        location = new Location(20d, 20d);
    }

    @Test
    public void ThenItShouldAcceptIt(){
        assertThat(area.isInArea(location), is(true));
    }

}
