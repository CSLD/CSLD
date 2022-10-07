package cz.larpovadatabaze;

import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import cz.larpovadatabaze.calendar.service.LarpCzEvents;
import cz.larpovadatabaze.calendar.service.LarpCzImport;
import cz.larpovadatabaze.donations.service.BankAccount;
import cz.larpovadatabaze.games.services.SimilarGames;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RegularTasks {
    private final static Logger logger = LogManager.getLogger();

    private Environment env;
    private SessionFactory sessionFactory;

    private SimilarGames similarGames;
    private LarpCzImport larpImport;
    private BankAccount bankAccount;
    private final GoogleCalendarEvents googleCalendarEvents;

    @Autowired
    public RegularTasks(Environment env, SessionFactory sessionFactory,
                        Events events, TokenSearch tokenSearch, SimilarGames similarGames, GoogleCalendarEvents googleCalendarEvents) {
        this.env = env;
        this.similarGames = similarGames;
        this.sessionFactory = sessionFactory;

        bankAccount = new BankAccount(sessionFactory);
        this.googleCalendarEvents = googleCalendarEvents;
        larpImport = new LarpCzImport(events, new LarpCzEvents(), sessionFactory, tokenSearch, googleCalendarEvents);
    }

    // Create / check calendar subscripton repeatedly
    @Scheduled(initialDelay = 10*1000, fixedDelay = 60*1000)
    public void calendarSync() {
        googleCalendarEvents.checkCalendarSubscription();
    }

    @Scheduled(cron = "0 0 2 * * *")
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

        try {
            sessionFactory.getCurrentSession()
                    .createSQLQuery("update csld_game " +
                            "set amount_of_ratings = subquery.ratings " +
                            "FROM ( " +
                            "         select sum(amounts.amount_of_ratings) as ratings, amounts.game_id " +
                            "         from ( " +
                            "                  select count(*) as amount_of_ratings, game_id " +
                            "                  from csld_rating " +
                            "                  group by game_id, rating " +
                            "                  having rating is not null) AS amounts " +
                            "         group by amounts.game_id) as subquery " +
                            "where csld_game.id = subquery.game_id;")
                    .executeUpdate();
        } catch (Exception ex) {
            logger.error("It wasn't possible to recalculate amount of ratings", ex);
        }
    }
}
