package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.SimilarGame;
import cz.larpovadatabaze.common.services.CRUDService;

import java.util.List;

public interface SimilarGames extends CRUDService<SimilarGame, Integer> {
    /**
     * It returns the information about the similarity of the games to the provided one.
     *
     * @param game Game to which we care about the similar games.
     * @return List of the similar games ordered by the similarity
     */
    List<Game> allForGame(Game game);

    /**
     * Recalculates similarity rating for all games in the database.
     * Careful. This is a lengthy process.
     */
    void recalculateForAll();
}
