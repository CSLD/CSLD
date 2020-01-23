package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.SimilarGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class SqlSimilarGamesIT extends WithDatabase {
    private SimilarGames underTest;

    @Before
    public void prepareService() {
        underTest = new SqlSimilarGames(
                sessionFactory,
                Mockito.mock(AppUsers.class)
        );
    }

    @Test
    public void returnAllGamesSimilarToSpecified() {
        List<Game> similar = underTest.allForGame(masqueradeEntities.firstMasquerade);

        assertThat(similar, hasSize(2));
        assertThat(similar, hasItems(
                masqueradeEntities.secondMasquerade,
                masqueradeEntities.bestMasquerade
        ));
    }
}
