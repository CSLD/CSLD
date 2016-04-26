package cz.larpovadatabaze.lang.component;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.lang.LanguagesInCookie;
import cz.larpovadatabaze.lang.components.ChooseLanguages;
import org.junit.Before;

public class GivenChooseLanguagesComponentIsDisplayed extends AcceptanceTest {
    ChooseLanguages languageChoice;
    LanguagesInCookie cookieStorage;

    @Before
    public void setUp() {
        languageChoice = new ChooseLanguages("languages", cookieStorage);

        tester.startComponentInPage(languageChoice);
    }
}
