package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.dao.GameDAO;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SqlGamesIT extends WithDatabase {
    Games underTest;

    @Before
    public void setUp() {
        super.setUp();

        AppUsers appUsers = Mockito.mock(AppUsers.class);
        GameDAO dao = new GameDAO(sessionFactory, appUsers);
        underTest = new SqlGames(dao, sessionFactory,
                Mockito.mock(FileService.class),
                Mockito.mock(ImageResizingStrategyFactoryService.class),
                Mockito.mock(Images.class));
    }

    @Test
    public void getLastGamesReturnsTheLatestGames() {
        List<Game> latest = underTest.getLastGames(2);

        assertThat(latest.size(), is(2));
    }
}
