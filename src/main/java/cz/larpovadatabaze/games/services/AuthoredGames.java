package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;

import java.util.Collection;
import java.util.List;

/**
 * The information about the games related to the ownership of the game by either Users
 * or groups.
 */
public interface AuthoredGames {
    /**
     * Return List of the other games of the author of specific game.
     *
     * @param game Game which serves as a source for the list of authors.
     * @return List of the uniques games authored by at least one author of the parameter game
     */
    List<Game> gamesOfAuthors(Game game);

    /**
     * Return paginated collection of the Games created by the specific author. The games are ordered by the
     * total rating.
     *
     * @param author The author which games will be filtered.
     * @param offset Index of the first game to return
     * @param limit  Limit of the games to return.
     * @return The collection of the games by given author satisfying the pagination criteria.
     */
    Collection<Game> getGamesOfAuthor(CsldUser author, int offset, int limit);

    /**
     * Total amount of the games created by specific author.
     *
     * @param author The author for filtering the games.
     * @return The amount of the games created by the author.
     */
    long getAmountOfGamesOfAuthor(CsldUser author);

    /**
     * Return paginated collection of the Games created by the specific group.
     *
     * @param groupOfAuthors The group of authors whose games will be filtered.
     * @param offset         Index of the first game to return
     * @param limit          Limit of the games to return.
     * @return The collection of the games by given author satisfying the pagination criteria.
     */
    Collection<Game> getGamesOfGroup(CsldGroup groupOfAuthors, int offset, int limit);

    /**
     * Total amount of the games created by specific group of authors.
     *
     * @param groupOfAuthors The author for filtering the games.
     * @return The amount of the games created by the author.
     */
    long getAmountOfGamesOfGroup(CsldGroup groupOfAuthors);

}
