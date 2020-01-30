package cz.larpovadatabaze.administration.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.administration.model.UserRatesOwnGameDto;
import cz.larpovadatabaze.administration.services.AdministeredUsers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class SqlAdministeredUsersIT extends WithDatabase {
    private AdministeredUsers underTest;

    @Before
    public void prepareAdministeredUsers() {
        underTest = new SqlAdministeredUsers(sessionFactory);
    }

    @Test
    public void returnThoseWhoRatedTheirOwnGames() {
        List<UserRatesOwnGameDto> ratedOwnGames = underTest.getUsersWhoRatesOwnGames();

        assertThat(ratedOwnGames, hasSize(5));
        assertThat(ratedOwnGames, hasItems(
                new UserRatesOwnGameDto(masqueradeEntities.bestMasquerade, masqueradeEntities.user),
                new UserRatesOwnGameDto(masqueradeEntities.bestMasquerade, masqueradeEntities.editor),
                new UserRatesOwnGameDto(masqueradeEntities.firstMasquerade, masqueradeEntities.editor),
                new UserRatesOwnGameDto(masqueradeEntities.bestMasquerade, masqueradeEntities.administrator),
                new UserRatesOwnGameDto(masqueradeEntities.firstMasquerade, masqueradeEntities.administrator)
        ));
    }
}
