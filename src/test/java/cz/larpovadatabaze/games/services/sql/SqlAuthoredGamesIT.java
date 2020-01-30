package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlAuthoredGamesIT extends WithDatabase {
    AuthoredGames underTest;

    @Before
    public void prepareAuthoredGames() {
        underTest = new SqlAuthoredGames(sessionFactory, Mockito.mock(AppUsers.class));
    }

    @Test
    public void returnGamesOfAllAuthorsOfTheGame() {
        List<Game> authoredGames = underTest.gamesOfAuthors(masqueradeEntities.secondMasquerade);

        assertThat(authoredGames, hasSize(44));
    }

    @Test
    public void returnFirstTwoGamesOfAuthor() {
        Collection<Game> authoredGames = underTest.getGamesOfAuthor(masqueradeEntities.user, 0, 2);

        assertThat(authoredGames, hasSize(1));
        assertThat(authoredGames, hasItems(
                masqueradeEntities.bestMasquerade
        ));
    }

    @Test
    public void amountOfAllGamesOfAuthor() {
        assertThat(
                underTest.getAmountOfGamesOfAuthor(masqueradeEntities.user),
                is(1L)
        );
    }

    @Test
    public void returnFirstTwoGamesOfGroup() {
        Collection<Game> authoredGames = underTest.getGamesOfGroup(masqueradeEntities.nosferatu, 0, 2);

        assertThat(authoredGames, hasSize(2));
        assertThat(authoredGames, hasItems(
                masqueradeEntities.firstMasquerade,
                masqueradeEntities.secondMasquerade
        ));
    }

    @Test
    public void amountOfAllGamesOfGroup() {
        assertThat(
                underTest.getAmountOfGamesOfGroup(masqueradeEntities.nosferatu),
                is(2L)
        );
    }
}
