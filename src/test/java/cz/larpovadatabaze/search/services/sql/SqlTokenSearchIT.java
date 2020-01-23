package cz.larpovadatabaze.search.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.TokenSearch;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class SqlTokenSearchIT extends WithDatabase {
    TokenSearch underTest;

    @Before
    public void prepareTokenSearch() {
        underTest = new SqlTokenSearch(
                sessionFactory,
                Mockito.mock(AppUsers.class)
        );
    }

    @Test
    public void findLabelsContainingToken() {
        List<Label> found = underTest.findLabels("am");
        assertThat(found, hasSize(3));
        assertThat(found, hasItems(masqueradeEntities.vampire, masqueradeEntities.chamber, masqueradeEntities.dramatic));
    }

    @Test
    public void findGroupsWithSpecificName() {
        List<CsldGroup> found = underTest.findGroups("Nosferatu");
        assertThat(found, hasSize(1));
        assertThat(found, hasItem(masqueradeEntities.nosferatu));
    }

    @Test
    public void findGameWithSpecificName() {
        List<Game> found = underTest.findGames("Masquerade 1");
        assertThat(found, hasSize(1));
        assertThat(found, hasItem(masqueradeEntities.firstMasquerade));
    }

    @Test
    public void findUserWithSpecificNameAndEmail() {
        List<CsldUser> found = underTest.findUsers("Irrelevant, editor@masquerade.test");
        assertThat(found, hasSize(1));
        assertThat(found, hasItem(masqueradeEntities.editor));

    }
}
