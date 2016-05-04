package cz.larpovadatabaze.lang.component;

import org.junit.Before;
import org.junit.Test;

public class WhenUserClicksOnIgnore extends GivenChooseLanguagesComponentIsDisplayed {
    @Before
    public void whenUserClicksOnIgnore(){
        tester.clickLink(languageChoice.getPath() + ":ignore");
    }

    @Test
    public void ThenTheComponentIsInvisible() {
        tester.assertInvisible(languageChoice.getPath());
    }
}
