package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class SqlGamesIT extends WithDatabase {
    Games underTest;

    @Before
    public void setUp() {
        super.setUp();

        AppUsers mockUsers = Mockito.mock(AppUsers.class);
        when(mockUsers.getLoggedUser()).thenReturn(masqueradeEntities.user);

        underTest = new SqlGames(sessionFactory,
                Mockito.mock(FileService.class),
                Mockito.mock(ImageResizingStrategyFactoryService.class),
                Mockito.mock(Images.class),
                mockUsers);
    }

    @Test
    public void getLastGamesReturnsTheMostRecentGames() {
        List<Game> latest = underTest.getLastGames(2);

        assertThat(latest, hasSize(2));
        assertThat(latest, hasItems(
                masqueradeEntities.firstMasquerade,
                masqueradeEntities.secondMasquerade
        ));
    }

    @Test
    public void mostPopularGamesReturnsGamesWithHighestRating() {
        List<Game> mostPopular = underTest.getMostPopularGames(1);

        assertThat(mostPopular, hasSize(1));
        assertThat(mostPopular, hasItems(
                masqueradeEntities.bestMasquerade
        ));
    }

    @Test
    public void getRatedByUserReturnsGamesRatedByUser() {
        Collection<Game> rated = underTest.getGamesRatedByUser(masqueradeEntities.editor.getId());

        assertThat(rated, hasSize(2));
        assertThat(rated, hasItems(
                masqueradeEntities.bestMasquerade,
                masqueradeEntities.firstMasquerade
        ));
    }

    @Test
    public void getCommentedReturnsTheCommentedGames() {
        Collection<Game> commented = underTest.getGamesCommentedByUser(masqueradeEntities.editor.getId());

        assertThat(commented, hasSize(1));
        assertThat(commented, hasItems(
                masqueradeEntities.secondMasquerade
        ));
    }

    @Test
    public void inserterOfTheGameCantEditGame() {
        assertThat(
                underTest.canEditGame(masqueradeEntities.firstMasquerade),
                is(false)
        );
    }

    @Test
    public void randomUserCantEditGame() {
        assertThat(
                underTest.canEditGame(masqueradeEntities.secondMasquerade),
                is(false)
        );
    }

    @Test
    public void deletedGameIsHidden() {
        AppUsers mockUsers = Mockito.mock(AppUsers.class);
        when(mockUsers.isEditor()).thenReturn(true);

        underTest = new SqlGames(sessionFactory,
                Mockito.mock(FileService.class),
                Mockito.mock(ImageResizingStrategyFactoryService.class),
                Mockito.mock(Images.class),
                mockUsers);

        // Only administrators get the game at all.
        assertThat(
                underTest.isHidden(masqueradeEntities.wrongMasquerade.getId()),
                is(true)
        );
    }
}
