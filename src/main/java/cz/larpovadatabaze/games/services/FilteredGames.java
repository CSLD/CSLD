package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.models.FilterGameDTO;

import java.util.List;

/**
 * Filter and paginate the games.
 */
public interface FilteredGames {
    /**
     * Return the paginated results containing the games that satisfy the filtering
     * criteria.
     *
     * @param filter The filtering options
     * @param offset What record the result should start with
     * @param limit  Limit of the items to be returned.
     * @return Page of the games satisfying the filtering criteria.
     */
    List<Game> paginated(FilterGameDTO filter, int offset, int limit);

    /**
     * The toatl amount of the games in the database satisfying given filtering options.
     *
     * @param filter The filtering options
     * @return Amount of the games in the db satisfying the filtering criteria
     */
    long totalAmount(FilterGameDTO filter);
}
