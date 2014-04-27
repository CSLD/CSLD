package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface RatingService extends GenericService<Rating> {
    public Rating getUserRatingOfGame(Integer userId, Integer gameId) throws WrongParameterException;

    double getAverageRating();

    void saveOrUpdate(Rating actualRating);

    int getAmountOfRatings();

    List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual);

    List<Game> getGamesRatedByUser(int userId);
}
