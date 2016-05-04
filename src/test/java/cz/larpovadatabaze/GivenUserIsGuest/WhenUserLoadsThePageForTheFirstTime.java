package cz.larpovadatabaze.GivenUserIsGuest;

import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.lang.components.ChooseLanguages;
import org.junit.Before;
import org.junit.Test;

public class WhenUserLoadsThePageForTheFirstTime extends AndHeIsHereForFirstTime {
    @Before
    public void setUp() {
        tester.startPage(HomePage.class);
    }

    @Test
    public void ThenTheLanguagePopupIsDisplayed() {
        tester.assertComponent("chooseFirstTimeLanguages", ChooseLanguages.class);
        tester.assertVisible("chooseFirstTimeLanguages");
    }
}
