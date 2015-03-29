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
    public Game getById(Integer id);

    /**
     * Flush game from Hibernate cache / session cache
     *
     * @param id Game id
     */
    public void evictGame(Integer id);

    public boolean addGame(Game game);

    List<Game> getSimilar(Game game);

    List<Game> gamesOfAuthors(Game game);

    List<Game> getByAutoCompletable(String gameName) throws WrongParameterException;

    Game getRandomGame();

    List<Game> getLastGames(int amountOfGames);

    int getAmountOfGames();

    List<Game> getFilteredGames(FilterGame filterGame, int offset, int limit);

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
     * Returns text version of hide/show game based on the game state.
     *
     * @param gameId Id of the game
     * @return String representing text to show/hide game
     */
    String getTextStateOfGame(int gameId);

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

    void deleteTranslation(GameHasLanguages toRemove);
}
