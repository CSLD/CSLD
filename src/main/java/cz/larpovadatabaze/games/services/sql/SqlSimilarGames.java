package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.entities.SimilarGame;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.SimilarGames;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Repository
@Transactional
public class SqlSimilarGames extends CRUD<SimilarGame, Integer> implements SimilarGames {
    private SessionFactory sessionFactory;
    private GenericHibernateDAO<Game, Integer> games;

    @Autowired
    public SqlSimilarGames(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(SimilarGame.class)));

        this.sessionFactory = sessionFactory;
        this.games = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Game.class));
    }

    @Override
    public List<Game> allForGame(Game game) {
        Criteria allSimilarities = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("idGame1", game.getId()))
                .addOrder(Order.desc("similarity"));

        // TODO: verify that deleted isn't returned.
        List<SimilarGame> similarGames = allSimilarities.list();
        List<Game> results = new ArrayList<>();
        for (SimilarGame similar : similarGames) {
            results.add(games.findById(similar.getIdGame2()));
        }

        return results;
    }

    @Override
    public void recalculateForAll() {
        // Delete all information about similarity.
        sessionFactory.getCurrentSession()
                .createQuery("delete from SimilarGame").executeUpdate();

        // Go twice through all the games. This is an expensive operation. And also don't want to load them all
        // to the memory
        ScrollableResults allGames =
                games.getExecutableCriteria().setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
        ScrollableResults secondForGamesToCompare =
                games.getExecutableCriteria().setReadOnly(true).scroll(ScrollMode.SCROLL_INSENSITIVE);

        while (allGames.next()) {
            Game actual = (Game) allGames.get(0);
            List<SimilarGame> similarGames = new ArrayList<>();
            while (secondForGamesToCompare.next()) {
                Game toCompare = (Game) secondForGamesToCompare.get(0);
                double similarity = similarityForTwoGames(actual, toCompare);
                similarGames.add(new SimilarGame(actual.getId(), toCompare.getId(), similarity));
            }
            similarGames.sort(Comparator.comparingDouble(SimilarGame::getSimilarity).reversed());

            List<SimilarGame> toStore = similarGames.subList(0, 9);
            toStore.forEach(crudRepository::saveOrUpdate);

            secondForGamesToCompare.first();
        }
    }

    double similarityForTwoGames(Game actual, Game toCompare) {
        // Similar labels
        double weightedLabelsSimilarity = similarityForLabels(actual.getLabels(), toCompare.getLabels()) * 0.4f;
        // Similar average rating
        double weightedRatingSimilarity = similarityByMathematicalDistance(actual.getAverageRating(), toCompare.getAverageRating()) * 0.2f;
        // Similar teams of authors
        double weightedAuthorsSimilarity = similarityForAuthors(actual.getAuthors(), toCompare.getAuthors()) * 0.2f;
        // Similar time
        double weightedTimeSimilarity = similarityByMathematicalDistance(
                actual.getYear(), toCompare.getYear()) * 0.1f;
        // Similar size
        double weightedSizeSimilarity = similarityByMathematicalDistance(
                actual.getPlayers(), toCompare.getPlayers()) * 0.1f;

        // Ascending order means that more to 0 means more similar game.
        // +1 means that if the required labels are shared, it brings better information
        return Math.round((weightedAuthorsSimilarity + weightedLabelsSimilarity + weightedSizeSimilarity +
                weightedTimeSimilarity + weightedRatingSimilarity) * 100.0) / 100.0;
    }

    private double similarityByMathematicalDistance(Integer current, Integer candidate) {
        if (current == null && candidate == null) {
            return 1;
        } else if (current == null || candidate == null) {
            return 0;
        }
        return similarityByMathematicalDistance(Double.valueOf(current), Double.valueOf(candidate));
    }

    private double similarityByMathematicalDistance(Double current, Double candidate) {
        if (current == null && candidate == null) {
            return 1;
        } else if (current == null || candidate == null) {
            return 0;
        } else if (current == 0.0 && candidate == 0.0) {
            return 1;
        }

        double size = Math.max(current, candidate);
        double distance = (Math.abs(current - candidate));
        return (1 - (distance / size));
    }

    private double similarityForAuthors(List<CsldUser> current, List<CsldUser> candidate) {
        double size = Math.max(current.size(), candidate.size());
        double distance = CollectionUtils.disjunction(current, candidate).size();
        return (1 - (distance / size));
    }

    private double similarityForLabels(List<Label> current, List<Label> candidate) {
        Collection<Label> differing = CollectionUtils.disjunction(current, candidate);
        long requiredLabels = differing.stream()
                .filter(label -> label.getRequired())
                .count();
        double distance = differing.size() + (double) requiredLabels;
        double size = Math.max(current.size(), candidate.size()) + (double) requiredLabels;
        return (1 - (distance / size));
    }
}
