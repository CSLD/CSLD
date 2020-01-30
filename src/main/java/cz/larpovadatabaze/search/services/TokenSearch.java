package cz.larpovadatabaze.search.services;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;

import java.util.List;

/**
 * Search by String tokens.
 */
public interface TokenSearch {
    /**
     * Finds labels with information related to the search token.
     *
     * @param token Token to use in the search for the labels
     * @return List of labels that reflect this token.
     */
    List<Label> findLabels(String token);

    /**
     * Find games with information related to the search token.
     *
     * @param token Token to use in the search for the games.
     * @return List of the Games that reflect this token.
     */
    List<Game> findGames(String token);

    /**
     * Find users with information related to the search token.
     *
     * @param token Token to use in the search for the user.
     * @return List of the Users that reflect this token.
     */
    List<CsldUser> findUsers(String token);

    /**
     * Find groups with information related to the search token.
     *
     * @param token Token to use in the search for the group
     * @return List of the groups that reflect the token.
     */
    List<CsldGroup> findGroups(String token);
}
