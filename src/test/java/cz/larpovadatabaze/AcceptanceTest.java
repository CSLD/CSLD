package cz.larpovadatabaze;

import cz.larpovadatabaze.services.builders.EntityBuilder;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    }

    @After
    public void tearDown() {
        session.close();
    }
}
