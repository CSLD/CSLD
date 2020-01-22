package cz.larpovadatabaze;

import cz.larpovadatabaze.common.services.builders.CzechMasqueradeBuilder;
import cz.larpovadatabaze.common.services.builders.MasqueradeEntities;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = RootTestWithDbConfig.class
)
abstract public class WithDatabase {
    protected static SessionHolder sessionHolder;

    protected Session session;
    protected MasqueradeEntities masqueradeEntities;

    @Autowired
    protected SessionFactory sessionFactory;
    @Autowired
    protected CzechMasqueradeBuilder masqueradeBuilder;
    @Autowired
    protected Flyway flyWay;


    // Before Each Test
    @Before
    public void setUp() {
        flyWay.migrate();
        session = sessionFactory.openSession();
        sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);

        masqueradeEntities = masqueradeBuilder.build();
    }

    @After
    public void tearDown() {
        flyWay.clean();

        session.close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);
    }
}
