package cz.larpovadatabaze.entities;

import java.io.Serializable;

/**
 * Provides game and its rating by user
 *
 * User: Michal Kara Date: 14.3.15 Time: 10:02
 */
public interface IGameWithRating extends Serializable {
    /**
     * @return Game in question
     */
    Game getGame();

    /**
     * @return Rating, NULL when not rated
     */
    Integer getRating();
}
