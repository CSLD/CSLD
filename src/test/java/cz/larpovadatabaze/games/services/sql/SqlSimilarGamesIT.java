package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.SimilarGame;
import cz.larpovadatabaze.games.services.SimilarGames;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class SqlSimilarGamesIT extends WithDatabase {
    private SimilarGames underTest;

    @BeforeEach
    public void prepareService() {
        underTest = new SqlSimilarGames(
                sessionFactory
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

    @Test
    public void recreateAllSimilarities() {
        Transaction current = sessionFactory.getCurrentSession().beginTransaction();
        underTest.recalculateForAll();
        current.commit();

        Long amountOfResults = (Long) DetachedCriteria.forClass(SimilarGame.class)
                .getExecutableCriteria(sessionFactory.getCurrentSession())
                .setProjection(Projections.rowCount())
                .uniqueResult();
        assertThat(amountOfResults, is(396L));
    }

    @Test
    public void compareTwoEqualGames() {
        double similarity = ((SqlSimilarGames) underTest).similarityForTwoGames(
                masqueradeEntities.firstMasquerade, masqueradeEntities.secondMasquerade);
        assertThat(similarity, is(1.0d));
    }

    @Test
    public void compareTwoProblematicGames() {
        double similarity = ((SqlSimilarGames) underTest).similarityForTwoGames(
                sessionFactory.getCurrentSession().get(Game.class, 5),
                sessionFactory.getCurrentSession().get(Game.class, 44));
        assertThat(similarity, is(0.7d));
    }
}
