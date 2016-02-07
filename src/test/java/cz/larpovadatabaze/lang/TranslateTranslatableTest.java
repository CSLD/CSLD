package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Game;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class TranslateTranslatableTest {
    private LanguageSolver solver;
    private LanguageSolver sessionSolver;

    @Before
    public void setUp(){
        solver = mock(LanguageSolver.class);
        sessionSolver = mock(LanguageSolver.class);
    }

    @Test
    public void translateIntoCurrentLanguageUsesSessionIfSessionLanguageAvailableInTranslations(){

    }

    @Test
    public void translateIntoAvailableToUserTranslatesToFirstAvailableFoundForGivenUser(){

    }

    @Test
    public void translateIntoDefaultReturnsFirstLanguageAddedToTheGame() {

    }
}
