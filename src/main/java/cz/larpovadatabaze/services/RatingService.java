package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 11:26
 */
public interface RatingService extends GenericService<Rating> {
    public Rating getUserRatingOfGame(Integer userId, Integer gameId) throws WrongParameterException;

    double getAverageRating();

    void saveOrUpdate(Rating actualRating);
}
