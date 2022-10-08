package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Ratings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlRatingsIT extends WithDatabase {
    private Ratings underTest;

    @BeforeEach
    public void createAndInitializeRatings() {
        underTest = new SqlRatings(sessionFactory,
                Mockito.mock(Games.class));
    }

    @Test
    public void returnExistingRatingOfTheGameByUser() {
        Rating existing = underTest.getUserRatingOfGame(
                masqueradeEntities.user.getId(),
                masqueradeEntities.bestMasquerade.getId()
        );

        assertThat(existing.getRating(), is(9));
    }

    @Test
    public void returnNullWhenRatingDoesntExist() {
        Rating nonexistent = underTest.getUserRatingOfGame(
                masqueradeEntities.user.getId(),
                masqueradeEntities.firstMasquerade.getId());

        assertThat(nonexistent, is(nullValue()));
    }

    @Test
    public void returnsRatingsOfLoggedUser() {
        List<Rating> ratings = underTest.getRatingsOfUser(
                masqueradeEntities.user,
                masqueradeEntities.user);

        assertThatTheUserRatingsAreReturned(ratings);
    }

    @Test
    public void returnEmptyListWhenSomeoneElseIsLogged() {
        List<Rating> ratings = underTest.getRatingsOfUser(
                masqueradeEntities.user,
                masqueradeEntities.editor);

        assertThat(ratings, hasSize(0));
    }

    @Test
    public void returnRatingsOfAnotherUserWhenAtLeastEditor() {
        List<Rating> ratings = underTest.getRatingsOfUser(
                masqueradeEntities.editor,
                masqueradeEntities.user);

        assertThatTheUserRatingsAreReturned(ratings);
    }

    private void assertThatTheUserRatingsAreReturned(List<Rating> ratings) {
        assertThat(ratings, hasSize(2));
        assertThat(ratings, hasItems(
                masqueradeEntities.userRatedBest,
                masqueradeEntities.userRatedSecond));
    }

    @Test
    public void returnNotratedForNull() {
        String result = underTest.getColor(null);
        assertThat(result, is("notrated"));
    }

    @Test
    public void returnAverageForRelevantRating() {
        String result = underTest.getColor(60D);
        assertThat(result, is("average"));
    }
}
