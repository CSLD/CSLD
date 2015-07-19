package cz.larpovadatabaze.services;

import java.util.Collection;
import java.util.List;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;

/**
 *
 */
public interface GameService extends GenericService<Game>, IIconReferenceProvider<Game> {
    Game getById(Integer id);

    /**
     * Flush game from Hibernate cache / session cache
     *
     * @param id Game id
     */
    void evictGame(Integer id);

    boolean addGame(Game game);

    Collection<Game> getSimilar(Game game);

    Collection<Game> gamesOfAuthors(Game game);

    Collection<Game> getByAutoCompletable(String gameName) throws WrongParameterException;

    Game getRandomGame();

    /**
     * It returns some amount of games sorted by added in descending order. This order is guaranteed regardless of chosen
     * collection implementation.
     *
     * @param amountOfGames How many games should be retrieved.
     * @return Ordered collection of games. In case there is no game to retrieve return empty collection.
     */
    Collection<Game> getLastGames(int amountOfGames);

    int getAmountOfGames();

    Collection<Game> getFilteredGames(FilterGame filterGame, int offset, int limit);

    long getAmountOfFilteredGames(FilterGame filterGame);

    Collection<Game> getGamesOfAuthor(CsldUser author, int first, int count);

    Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count);

    long getAmountOfGamesOfAuthor(CsldUser author);

    long getAmountOfGamesOfGroup(CsldGroup csldGroup);

    boolean saveOrUpdate(Game game);

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

    Collection<Game> getMostPopularGames(int amountOfGames);

    void deleteTranslation(GameHasLanguages toRemove);
}
