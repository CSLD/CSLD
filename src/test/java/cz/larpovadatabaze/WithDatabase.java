package cz.larpovadatabaze;

import cz.larpovadatabaze.common.services.builders.CzechMasqueradeBuilder;
import cz.larpovadatabaze.common.services.builders.MasqueradeEntities;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 */
@SpringBootTest
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
    @BeforeEach
    public void setUp() {
        flyWay.migrate();
        session = sessionFactory.openSession();
        sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);

        masqueradeEntities = masqueradeBuilder.build();
    }

    @AfterEach
    public void tearDown() {
        flyWay.clean();

        session.close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);
    }
}
