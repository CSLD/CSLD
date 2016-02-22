package cz.larpovadatabaze;

import cz.larpovadatabaze.api.Entity;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import cz.larpovadatabaze.services.builders.EntityBuilder;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;
import java.util.Map;

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
    @Autowired
    protected CzechMasqueradeBuilder masqueradeBuilder;


    @Before
    public void setUp(){
        if(tester == null) {
            tester = new WicketTester(csld);
        }
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        sessionHolder = new SessionHolder(session);
        masqueradeBuilder.build();
        if(getLoggedUser() != null) {
            TestUtils.logUser(getLoggedUser()); // This way it is at the level of user per class. Probably makes sense.
        }
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
    }

    /**
     * Hook allowing us to decide whether user should be logged for given test and if yes, how exactly is should happen.
     * @return User logged in or null for guest.
     */
    protected CsldUser getLoggedUser() {
        return null;
    }

    @After
    public void tearDown() {
        cleanDatabase();
        session.close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        TestUtils.logoutUser();
    }

    private void cleanDatabase(){
        session.getTransaction().begin();

        Collection<Entity> events = session.createQuery("from Event").list();
        for(Entity event: events) {
            session.delete(event);
        }

        session.flush();
        session.getTransaction().commit();
    }
}
