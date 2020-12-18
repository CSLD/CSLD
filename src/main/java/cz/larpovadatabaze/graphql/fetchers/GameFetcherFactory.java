package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.SimilarGames;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for Game(s) fetchers
 */
@Component
public class GameFetcherFactory {
    private final Games games;
    private final SimilarGames similarGames;
    private final AuthoredGames authoredGames;

    @Autowired
    GameFetcherFactory(Games games, SimilarGames similarGames, AuthoredGames authoredGames) {
        this.games = games;
        this.similarGames = similarGames;
        this.authoredGames = authoredGames;
    }

    public DataFetcher<List<Game>> createLastAddedGamesFetcher() {
        return dataFetchingEnvironment -> games.getLastGames(6);
    }

    public DataFetcher<List<Game>> createMostPopularGamesFetcher() {
        return dataFetchingEnvironment -> games.getMostPopularGames(6);
    }

    public DataFetcher<Game> createGameByIdFetcher() {
        return dataFetchingEnvironment -> games.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("gameId")));
    }

    public DataFetcher<List<Game>> createGameSimilarGamesFetcher() {
        return dataFetchingEnvironment -> {
            Game game = dataFetchingEnvironment.getSource();
            return similarGames.allForGame(game);
        };
    }

    public DataFetcher<List<Game>> createGameGamesOfAuthorsFetcher() {
        return dataFetchingEnvironment -> {
            Game game = dataFetchingEnvironment.getSource();
            return authoredGames.gamesOfAuthors(game);
        };
    }
}
