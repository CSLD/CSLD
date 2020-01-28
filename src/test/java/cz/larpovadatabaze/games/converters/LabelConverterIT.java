package cz.larpovadatabaze.games.converters;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.sql.SqlTokenSearch;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class LabelConverterIT extends WithDatabase {
    private LabelConverter underTest;

    @Before
    public void prepareLabelConverter() {
        underTest = new LabelConverter(
                new SqlTokenSearch(sessionFactory, Mockito.mock(AppUsers.class))
        );
    }

    @Test
    public void convertExistingLabelToProperObject() {
        Label found = underTest.convertToObject("Emotional", Locale.ENGLISH);

        assertThat(found, is(masqueradeEntities.emotional));
    }

    @Test
    public void convertNonExistentLabelToNull() {
        Label found = underTest.convertToObject("Nonexistent", Locale.ENGLISH);

        assertThat(found, is(nullValue()));
    }
}
