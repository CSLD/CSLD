package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.services.CsldUsers;
import cz.larpovadatabaze.services.Games;
import cz.larpovadatabaze.services.Ratings;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 *
 */
@Repository
@Transactional
public class SqlRatings extends CRUD<Rating, Integer> implements Ratings {
    private final CsldUsers csldUsers;
    private final GenericHibernateDAO<UserPlayedGame, Integer> statesOfGameForUsers;
    private final Games games;

    @Autowired
    public SqlRatings(SessionFactory sessionFactory, CsldUsers csldUsers, Games games) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Rating.class)));

        this.csldUsers = csldUsers;
        this.statesOfGameForUsers = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(UserPlayedGame.class));
        this.games = games;
    }

    public Rating getUserRatingOfGame(Integer userId, Integer gameId) {
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.conjunction()
                .add(Restrictions.eq("game.id", gameId))
                .add(Restrictions.eq("user.id", userId));
        return crudRepository.findSingleByCriteria(criterions);
    }

    @Override
    public void remove(Rating toRemove) {
        crudRepository.makeTransient(toRemove);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        games.evictGame(toRemove.getGame().getId());
    }

    // TODO: Remove SQL or move back to Repository
    @Override
    public double getAverageRating() {
        Session session = crudRepository.getCurrentSession();
        String sqlQuery = "select csld_count_average()";
        NativeQuery query = session.createSQLQuery(sqlQuery);
        if (query.uniqueResult() == null) {
            return 0.0;
        } else {
            return (Double) query.uniqueResult();
        }
    }

    @Override
    public boolean saveOrUpdate(Rating actualRating) {
        actualRating.setAdded(new Timestamp(new Date().getTime()));
        crudRepository.saveOrUpdate(actualRating);

        // Mark that user played game
        UserPlayedGame upg = getUserPlayedGame(actualRating.getGame().getId(), actualRating.getUser().getId());
        if (upg == null) {
            upg = new UserPlayedGame();
            upg.setGame(actualRating.getGame());
            upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            upg.setPlayerOfGame(actualRating.getUser());
        } else {
            if (upg.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.NONE)) {
                upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            }
        }
        boolean result = statesOfGameForUsers.saveOrUpdate(upg);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        games.evictGame(actualRating.getGame().getId());
        return result;
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        if (logged == null || (!logged.getId().equals(actual.getId()) && !csldUsers.isLoggedAtLeastEditor())) {
            return new ArrayList<>();
        } else {
            Criteria criteria = crudRepository.getExecutableCriteria()
                    .add(Restrictions.eq("user.id", actual.getId()))
                    .addOrder(Order.desc("rating"));

            Set<Rating> unsorted = new LinkedHashSet<>(criteria.list());
            return new ArrayList<>(unsorted);
        }
    }

    @Override
    public void delete(Rating rating) {
        crudRepository.makeTransient(rating);

        games.evictGame(rating.getGame().getId());
    }

    @Override
    public UserPlayedGame getUserPlayedGame(int gameId, int userId) {
        Criteria stateOfGame = statesOfGameForUsers.getExecutableCriteria()
                .add(Restrictions.eq("game.id", gameId))
                .add(Restrictions.eq("playerOfGame.id", userId));
        return (UserPlayedGame) stateOfGame.uniqueResult();
    }

    @Override
    public boolean saveOrUpdate(UserPlayedGame stateOfGame) {
        boolean result = statesOfGameForUsers.saveOrUpdate(stateOfGame);

        if (stateOfGame.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.NONE)) {
            // Remove rating
            Rating rating = getUserRatingOfGame(stateOfGame.getPlayerOfGame().getId(), stateOfGame.getGame().getId());
            if (rating != null) {
                remove(rating);
            }
        }

        return result;
    }
}
