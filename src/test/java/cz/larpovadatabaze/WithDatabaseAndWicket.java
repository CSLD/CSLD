package cz.larpovadatabaze;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = RootTestWithDbConfig.class
)
abstract public class WithDatabaseAndWicket extends WithDatabase {
    protected static WicketTester tester;

    @Autowired
    private Csld csld;

    // Before Each Test
    @Before
    public void setUp() {
        if (tester == null) {
            tester = new WicketTester(csld);
        }
        super.setUp();

        if (getLoggedUser() != null) {
            TestUtils.logUser(getLoggedUser()); // This way it is at the level of user per class. Probably makes sense.
        }
        CsldAuthenticatedWebSession.get().setLocale(Locale.forLanguageTag("cs"));
    }

    /**
     * Hook allowing us to decide whether user should be logged for given test and if yes, how exactly is should happen.
     *
     * @return User logged in or null for guest.
     */
    protected CsldUser getLoggedUser() {
        return null;
    }

    @After
    public void tearDown() {
        super.tearDown();

        TestUtils.logoutUser();
    }
}
