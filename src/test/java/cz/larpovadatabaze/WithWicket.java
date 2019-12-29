package cz.larpovadatabaze;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = RootTestWicketContext.class
)
public abstract class WithWicket {
    protected static WicketTester tester;
    @Autowired
    Csld underTest;

    @Before
    public void setUp() {
        if (tester == null) {
            tester = new WicketTester(underTest);
        }

        CsldAuthenticatedWebSession.get().setLocale(Locale.forLanguageTag("cs"));
    }

    /**
     * Hook allowing us to decide whether user should be logged for given test and if yes, how exactly is should happen.
     *
     * @return User logged in or null for guest.
     */
    protected void logUser(CsldUser currentlyLogged) {
        TestUtils.logUser(currentlyLogged);
    }

    @After
    public void tearDown() {
        TestUtils.logoutUser();
    }
}
