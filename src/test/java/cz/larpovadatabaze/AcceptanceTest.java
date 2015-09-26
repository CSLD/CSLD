package cz.larpovadatabaze;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.builders.EntityBuilder;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = RootConfig.class
)
abstract public class AcceptanceTest {
    static {
        System.setProperty("props.path", "src/test/config");
    }

    protected static WicketTester tester;
    protected static SessionHolder sessionHolder;

    protected Session session;

    @Autowired
    private Csld csld;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    protected EntityBuilder persistenceStore;

    @Before
    public void setUp(){
        if(tester == null) {
            tester = new WicketTester(csld);
        }
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        sessionHolder = new SessionHolder(session);
        if(getLoggedUser() != null) {
            TestUtils.logUser(getLoggedUser()); // This way it is at the level of user per class. Probably makes sense.
        }
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
    }

    protected CsldUser getLoggedUser() {
        return null;
    }

    @After
    public void tearDown() {
        session.close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        TestUtils.logoutUser();
    }
}
