package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.entities.UserPlayedGame;
import cz.larpovadatabaze.common.exceptions.WrongParameterException;
import cz.larpovadatabaze.common.services.CRUDService;

import java.util.List;

/**
 *
 */
public interface Ratings extends CRUDService<Rating, Integer> {
    Rating getUserRatingOfGame(Integer userId, Integer gameId) throws WrongParameterException;

    double getAverageRating();

    List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual);

    /**
     * Deletes rating from the database. Associated game is also evicted from the hibernate
     * cache as some values are computer by triggers.
     *
     * @param rating rating to hide.
     */
    void delete(Rating rating);

    UserPlayedGame getUserPlayedGame(int gameId, int userId);

    boolean saveOrUpdate(UserPlayedGame stateOfGame);
}
