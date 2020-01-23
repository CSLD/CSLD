package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.CRUDService;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;
import cz.larpovadatabaze.games.models.FilterGame;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface Games extends CRUDService<Game, Integer>, IIconReferenceProvider<Game> {
    /**
     * Flush game from Hibernate cache / session cache
     *
     * @param id Game id
     */
    void evictGame(Integer id);

    boolean addGame(Game game);

    /**
     * @param game
     * @return
     */
    List<Game> getSimilar(Game game);

    // Fitlered Games?
    List<Game> gamesOfAuthors(Game game);

    List<Game> getLastGames(int amountOfGames);

    List<Game> getFilteredGames(FilterGame filterGame, int offset, int limit);

    Collection<Game> getGamesOfAuthor(CsldUser author, int first, int count);

    Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count);

    // Statistics about filtered Games
    long getAmountOfFilteredGames(FilterGame filterGame);

    long getAmountOfGamesOfAuthor(CsldUser author);

    long getAmountOfGamesOfGroup(CsldGroup csldGroup);

    /**
     * @return Currently logged user can edit game
     */
    boolean canEditGame(Game game);

    Collection<Game> getGamesCommentedByUser(int userId);

    /**
     * It returns whether the game with given id is hidden. It returns hidden if this game does not exist.
     *
     * @param gameId Id of the game
     * @return False if the fame is visible and exists.
     */
    boolean isHidden(int gameId);

    /**
     * It changes state of the game from hidden to shown and back.
     *
     * @param gameId Id which state will be shown.
     */
    void toggleGameState(int gameId);

    /**
     * It returns list of all games given user rated.
     *
     * @param userId Id of the user.
     * @return List of all rated games.
     */
    Collection<Game> getGamesRatedByUser(int userId);

    List<Game> getMostPopularGames(int amountOfGames);
}
