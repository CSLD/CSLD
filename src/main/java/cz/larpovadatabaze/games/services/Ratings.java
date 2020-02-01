package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.services.CRUDService;

import java.util.List;

/**
 *
 */
public interface Ratings extends CRUDService<Rating, Integer> {
    /**
     * Return object representing the rating of the user towards the game.
     *
     * @param userId Id of the user
     * @param gameId Id of the game
     * @return Rating from the user or null, if there is no rating.
     */
    Rating getUserRatingOfGame(Integer userId, Integer gameId);

    /**
     * Return all ratings of specific user, if you have the proper rights.
     * The proper rights means that you want to display your ratings or that
     * your role is editor or higher.
     *
     * @param logged Currently logged user
     * @param actual The user for whom you lookup the ratings
     * @return List of the ratings. If there are none, the empty list is returned.
     */
    List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual);

    /**
     * Return the color associated with rating of this game.
     *
     * @param rating The value of the rating between 0 and 100. Null is accepted and represented as 0
     * @return String representing the valid name for the rating of the game
     */
    String getColor(Double rating);

    /**
     * Return ratings associated with game that have rating other than null ordered
     * by the rating
     *
     * @param game Game to limit ratings.
     * @return Ordered list of ratings.
     */
    List<Rating> getRatingsOfGame(Game game);

    /**
     * Remove all ratings created by specific user.
     *
     * @param toRemove User whose ratings should be removed.
     */
    void removeForUser(CsldUser toRemove);
}
