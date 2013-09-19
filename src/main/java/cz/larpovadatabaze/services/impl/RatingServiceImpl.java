package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.RatingDAO;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.RatingService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 11:27
 */
@Repository
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingDAO ratingDAO;

    public Rating getUserRatingOfGame(Integer userId, Integer gameId)
    {
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.conjunction()
                .add(Restrictions.eq("gameId", gameId))
                .add(Restrictions.eq("userId", userId));
        return ratingDAO.findSingleByCriteria(criterions);
    }

    @Override
    public List<Rating> getAll()
    {
        List<Rating> ratings = ratingDAO.findAll();
        ratingDAO.flush();
        return ratings;
    }

    @Override
    public void remove(Rating toRemove) {
        ratingDAO.makeTransient(toRemove);
    }

    @Override
    public List<Rating> getUnique(Rating example) {
        return ratingDAO.findByExample(example, new String[]{});
    }

    @Override
    public double getAverageRating() {
        return ratingDAO.getAverageRating();
    }

    @Override
    public void saveOrUpdate(Rating actualRating) {
        ratingDAO.saveOrUpdate(actualRating);
    }

    @Override
    public int getAmountOfRatings() {
        return ratingDAO.getAmountOfRatings();
    }
}
