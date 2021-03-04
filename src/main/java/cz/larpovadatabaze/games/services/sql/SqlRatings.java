package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.CsldRoles;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
    private final Games games;

    @Autowired
    public SqlRatings(SessionFactory sessionFactory, Games games) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Rating.class)));

        this.games = games;
    }

    @Override
    public Rating getUserRatingOfGame(Integer userId, Integer gameId) {
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.conjunction()
                .add(Restrictions.eq("game.id", gameId))
                .add(Restrictions.eq("user.id", userId));
        return crudRepository.findSingleByCriteria(criterions);
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        if (logged == null) {
            return new ArrayList<>();
        }

        boolean isActualLogged = logged.equals(actual);
        boolean isAtLeastEditor = logged.getRole() >= CsldRoles.EDITOR.getRole();
        if (isActualLogged || isAtLeastEditor) {
            Criteria criteria = crudRepository.getExecutableCriteria()
                    .add(Restrictions.eq("user.id", actual.getId()))
                    .add(Restrictions.isNotNull("rating"))
                    .addOrder(Order.desc("rating"));

            Set<Rating> unsorted = new LinkedHashSet<Rating>(criteria.list());
            return new ArrayList<>(unsorted);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getColor(Double rating) {
        if (rating == null || rating < 0) {
            return "notrated";
        }
        if (rating >= 0 && rating <= 40) {
            return "mediocre";
        } else if (rating > 40 && rating <= 70) {
            return "average";
        } else if (rating > 70) {
            return "great";
        } else {
            return "notrated";
        }
    }

    @Override
    public String getColorForGame(Game game) {
        if (game.getAmountOfRatings() < 5) {
            return getColor(null);
        } else {
            return getColor(game.getAverageRating());
        }
    }

    @Override
    public List<Rating> getRatingsOfGame(Game game) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("game", game))
                .add(Restrictions.isNotNull("rating"))
                .addOrder(Order.desc("rating"));

        return criteria.list();
    }

    @Override
    public void removeForUser(CsldUser toRemove) {
        List<Rating> ratedBy = crudRepository.findByCriteria(
                Restrictions.eq("user", toRemove));

        ratedBy.forEach(this::remove);
    }

    @Override
    public void remove(Rating toRemove) {
        crudRepository.delete(toRemove);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        games.evictGame(toRemove.getGame().getId());
    }

    @Override
    @Transactional
    public boolean saveOrUpdate(Rating actualRating) {
        actualRating.setAdded(new Timestamp(new Date().getTime()));
        if (actualRating.getStateEnum() != Rating.GameState.WANT_TO_PLAY
                && actualRating.getRating() != null) {
            actualRating.setStateEnum(Rating.GameState.PLAYED);
        }

        // If the author rates own game, flag it
        if (actualRating.getRating() != null && actualRating.getGame() != null) {
            CsldUser addedBy = actualRating.getUser();
            List<CsldUser> authors = actualRating.getGame().getAuthors();
            actualRating.setByAuthor(authors.indexOf(addedBy) != -1);
        }

        crudRepository.saveOrUpdate(actualRating);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        games.evictGame(actualRating.getGame().getId());
        return true;
    }
}
