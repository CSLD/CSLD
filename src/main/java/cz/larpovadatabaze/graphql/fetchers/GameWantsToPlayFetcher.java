package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetch users who want to play the game
 */
public class GameWantsToPlayFetcher implements DataFetcher<List<CsldUser>> {
    @Override
    public List<CsldUser> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Game game = dataFetchingEnvironment.getSource();

        return game.getRatings().stream()
                .filter(gameState -> gameState.getStateEnum().equals(Rating.GameState.WANT_TO_PLAY))
                .map(Rating::getUser)
                .collect(Collectors.toList());
    }
}
