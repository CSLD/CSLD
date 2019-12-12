package cz.larpovadatabaze;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.builders.CzechMasqueradeBuilder;
import cz.larpovadatabaze.services.builders.EntityBuilder;
import org.apache.wicket.util.tester.WicketTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.util.Locale;

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
        CsldAuthenticatedWebSession.get().setLocale(Locale.forLanguageTag("cs"));
    }

    /**
     * Hook allowing us to decide whether user should be logged for given test and if yes, how exactly is should happen.
     * @return User logged in or null for guest.
     */
    protected CsldUser getLoggedUser() {
        return null;
    }

    @After
    public void tearDown() throws Exception {
        cleanDatabase();

        session.close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);

        TestUtils.logoutUser();
    }

    private void cleanDatabase() throws Exception {
        Connection toDatabase = sessionFactory.
                getSessionFactoryOptions().getServiceRegistry().
                getService(ConnectionProvider.class).getConnection();
        IDatabaseConnection connection = new DatabaseConnection(toDatabase);
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        IDataSet databaseDataSet = connection.createDataSet();
        try {
            // Remove all the constraints.
            new DeleteAllIgnoringConstraints().execute(connection, databaseDataSet);
        } finally {
            connection.close();
        }
    }
}
