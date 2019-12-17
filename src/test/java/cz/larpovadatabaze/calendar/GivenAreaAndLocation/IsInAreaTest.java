package cz.larpovadatabaze.calendar.GivenAreaAndLocation;

import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.Location;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IsInAreaTest {
    private BoundingBox area;
    private Location location;

    @Before
    public void WhenTheLocationIsInTheArea() {
        area = new BoundingBox(new Location(10d, 10d), new Location(20d, 20d));
        location = new Location(15d, 15d);
    }

    @Test
    public void ThenItShouldAcceptIt(){
        assertThat(area.isInArea(location), is(true));
    }
}
