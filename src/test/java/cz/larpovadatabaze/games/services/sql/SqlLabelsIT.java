package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SqlLabelsIT extends WithDatabase {
    private Labels underTest;

    @BeforeEach
    public void createAndInitializeLabels() {
        underTest = new SqlLabels(sessionFactory);
    }

    @Test
    public void getRequiredReturnsOnlyRequired() {
        List<Label> requiredLabels = underTest.getRequired();
        assertThat(requiredLabels.size(), is(2));
        assertThat(requiredLabels, hasItems(masqueradeEntities.dramatic, masqueradeEntities.chamber));
    }

    @Test
    public void getOptionalReturnsOnlyOptional() {
        List<Label> optionalLabels = underTest.getOptional();
        assertThat(optionalLabels.size(), is(2));
        assertThat(optionalLabels, hasItems(masqueradeEntities.emotional, masqueradeEntities.vampire));
    }

    @Test
    // The person who didn't add the label, can't use it.
    public void getAuthorizedOptionalReturnsOptionalFurtherFiltered() {
        List<Label> validLabels = underTest.getAuthorizedOptional(masqueradeEntities.anna);
        assertThat(validLabels.size(), is(1));
        assertThat(validLabels, hasItem(masqueradeEntities.vampire));
    }

    @Test
    public void getAuthorizedRequiredReturnsRequiredFurtherFiltered() {
        List<Label> validLabels = underTest.getAuthorizedRequired(masqueradeEntities.anna);
        assertThat(validLabels.size(), is(1));
        assertThat(validLabels, hasItem(masqueradeEntities.dramatic));
    }
}
