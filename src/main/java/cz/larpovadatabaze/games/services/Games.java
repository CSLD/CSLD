package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.CRUDService;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;
import cz.larpovadatabaze.graphql.GraphQLUploadedFile;

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

    /**
     * Return games ordered based on the time of creation limited by the amount of the
     * requested games
     *
     * @param limit Limit of the games to the return
     * @return List of the latest added games.
     */
    List<Game> getLastGames(int limit);

    /**
     * Return games sorted by the rating limited by the limit for the requested games.
     *
     * @param limit Limit for the games to return.
     * @return List of the games.
     */
    List<Game> getMostPopularGames(int limit);

    /**
     * It returns list of all games given user rated.
     *
     * @param userId Id of the user.
     * @return List of all rated games.
     */
    Collection<Game> getGamesRatedByUser(int userId);

    /**
     * It returns all games, which were commented by single user. The games are distinct.
     *
     * @param userId Id of the user, whose games we want to get.
     * @return List of games this user commented.
     */
    Collection<Game> getGamesCommentedByUser(int userId);

    /**
     * TODO: Maybe moved to the Authorized games?
     * The user who is author of the game or administrator can edit the game. Nobody else.
     *
     * @return Currently logged user can edit game
     */
    boolean canEditGame(Game game);

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

    boolean saveOrUpdate(Game model, GraphQLUploadedFile coverImageUpload);
}
