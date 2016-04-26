package cz.larpovadatabaze.GivenUserIsGuest;

import org.junit.Before;

public class AndHeIsHereForFirstTime extends GivenUserIsGuest {
    @Before
    public void setUp(){
        // Clean cookie, It isn't necessary. The cookies are cleansed with new instance of WicketTester.
    }
}
