package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import cz.larpovadatabaze.games.services.FilteredGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class SqlFilteredGamesIT extends WithDatabase {
    private FilteredGames underTest;

    @BeforeEach
    public void prepareFilteredGames() {
        underTest = new SqlFilteredGames(
                sessionFactory,
                Mockito.mock(AppUsers.class)
        );
    }

    @Test
    public void getFirstTwoWithRequiredLabels() {
        List<Label> requiredLabels = new ArrayList<>();
        requiredLabels.add(masqueradeEntities.chamber);

        List<Game> filtered = underTest.paginated(new FilterGameDTO(requiredLabels), 0, 2);
        assertThat(filtered, hasSize(1));
        assertThat(filtered, hasItem(masqueradeEntities.bestMasquerade));
    }

    @Test
    public void getFirstTwoOrderedByRating() {
        List<Game> filtered = underTest.paginated(new FilterGameDTO(FilterGameDTO.OrderBy.RATING_DESC), 0, 2);
        assertThat(filtered, hasSize(2));
        assertThat(filtered, hasItems(
                masqueradeEntities.bestMasquerade,
                masqueradeEntities.firstMasquerade
        ));
    }

    @Test
    public void getFirstTwoOrderedByAdded() {
        List<Game> filtered = underTest.paginated(new FilterGameDTO(FilterGameDTO.OrderBy.ADDED_DESC), 0, 2);
        assertThat(filtered, hasSize(2));
        assertThat(filtered, hasItems(
                masqueradeEntities.firstMasquerade,
                masqueradeEntities.secondMasquerade
        ));
    }

    @Test
    public void getFirstTwoOrderedByAmountOfComments() {
        List<Game> filtered = underTest.paginated(new FilterGameDTO(FilterGameDTO.OrderBy.ADDED_DESC), 0, 2);
        assertThat(filtered, hasSize(2));
        assertThat(filtered, hasItems(
                masqueradeEntities.secondMasquerade,
                masqueradeEntities.firstMasquerade
        ));
    }

    @Test
    public void getAmountOfFilteredByAmountOfComments() {
        long amount = underTest.totalAmount(new FilterGameDTO(FilterGameDTO.OrderBy.ADDED_DESC));

        assertThat(amount, is(43L));
    }
}
