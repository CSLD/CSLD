package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

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

    int getAmountOfRatings();

    Integer getRatingsForGame(Integer id);

    List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual);

    List<Game> getGamesRatedByUser(int userId);
}
