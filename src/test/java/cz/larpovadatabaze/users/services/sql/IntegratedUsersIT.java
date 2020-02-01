package cz.larpovadatabaze.users.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.calendar.service.sql.SqlEvents;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.games.services.sql.*;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class IntegratedUsersIT extends WithDatabase {
    private CsldUsers underTest;
    private AppUsers appUsers;

    @Before
    public void prepareUsers() {
        appUsers = Mockito.mock(AppUsers.class);
        FileService files = Mockito.mock(FileService.class);
        ImageResizingStrategyFactoryService imageResizingStrategyFactoryService =
                Mockito.mock(ImageResizingStrategyFactoryService.class);
        Images images = Mockito.mock(Images.class);

        Games games = new SqlGames(sessionFactory,
                files, imageResizingStrategyFactoryService, images, appUsers);
        Ratings ratings = new SqlRatings(sessionFactory, games);
        Comments comments = new SqlComments(sessionFactory, games, appUsers);
        Upvotes upvotes = new SqlUpvotes(sessionFactory);
        Events events = new SqlEvents(sessionFactory, Mockito.mock(GamesWithState.class));
        Labels labels = new SqlLabels(sessionFactory);
        Photos photos = new SqlFilePhotos(sessionFactory, files, games, imageResizingStrategyFactoryService);
        underTest = new SqlCsldUsers(
                sessionFactory,
                Mockito.mock(Images.class),
                Mockito.mock(MailService.class),
                Mockito.mock(EmailAuthentications.class),
                files,
                imageResizingStrategyFactoryService,
                ratings,
                comments,
                upvotes,
                events,
                labels,
                photos,
                appUsers,
                games
        );
    }

    @Test
    public void deleteExistingUser() {
        when(appUsers.isAtLeastEditor()).thenReturn(true);

        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        underTest.remove(masqueradeEntities.administrator);
        transaction.commit();

        // Verify that the data returned
    }
}
