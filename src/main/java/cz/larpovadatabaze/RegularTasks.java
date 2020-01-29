package cz.larpovadatabaze;

import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.LarpCzEvents;
import cz.larpovadatabaze.calendar.service.LarpCzImport;
import cz.larpovadatabaze.donations.service.BankAccount;
import cz.larpovadatabaze.games.services.SimilarGames;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RegularTasks {
    private final static Logger logger = Logger.getLogger(RegularTasks.class);

    private Environment env;

    private SimilarGames similarGames;
    private LarpCzImport larpImport;
    private BankAccount bankAccount;

    @Autowired
    public RegularTasks(Environment env, SessionFactory sessionFactory,
                        Events events, TokenSearch tokenSearch, SimilarGames similarGames) {
        this.env = env;
        this.similarGames = similarGames;

        bankAccount = new BankAccount(sessionFactory);
        larpImport = new LarpCzImport(events, new LarpCzEvents(), sessionFactory, tokenSearch);
    }

    @Scheduled(cron = "0 2 * * *")
    public void nightly() {
        logger.info("Run Nightly Tasks");

        if (env.getProperty("csld.integrate_bank", Boolean.class)) {
            try {
                bankAccount.importData();
            } catch (Exception ex) {
                logger.error("It wasn't possible to import bank data. ", ex);
            }
        }

        if (env.getProperty("csld.integrate_calendar", Boolean.class)) {
            try {
                larpImport.importEvents();
            } catch (Exception ex) {
                logger.error("It wasn't possibel to import larp cz events", ex);
            }
        }

        try {
            similarGames.recalculateForAll();
        } catch (Exception ex) {
            logger.error("It wasn't possible to recalculate similar games", ex);
        }
    }
}
