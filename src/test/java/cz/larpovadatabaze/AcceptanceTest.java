package cz.larpovadatabaze;

import cz.larpovadatabaze.services.builders.EntityBuilder;
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
public class AcceptanceTest {
    static {
        System.setProperty("props.path", "src/test/config");
    }

    protected WicketTester tester;
    protected Session session;

    @Autowired
    private Csld csld;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    protected EntityBuilder persistenceStore;

    @Before
    public void setUp(){
        tester = new WicketTester(csld);
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        SessionHolder sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
    }

    @After
    public void tearDown() {
        session.close();
    }
}
