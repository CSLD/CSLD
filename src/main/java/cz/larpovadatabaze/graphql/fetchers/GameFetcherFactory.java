package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.GamesWithState;
import cz.larpovadatabaze.games.services.SimilarGames;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
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
    private final GamesWithState gamesWithState;
    private final GameMutationFetcherFactory gameMutationFetcherFactory;

    private static final String[] ACCESS_EDIT_DELETE = new String[]{"Edit", "Delete"};

    @Autowired
    GameFetcherFactory(Games games, SimilarGames similarGames, AuthoredGames authoredGames, GamesWithState gamesWithState, GameMutationFetcherFactory gameMutationFetcherFactory) {
        this.games = games;
        this.similarGames = similarGames;
        this.authoredGames = authoredGames;
        this.gamesWithState = gamesWithState;
        this.gameMutationFetcherFactory = gameMutationFetcherFactory;
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

    public DataFetcher<Collection<Game>> createUserAuthoredGamesFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return authoredGames.getGamesOfAuthor(user, 0, 10000);
        };
    }

    public DataFetcher<Collection<Game>> createGroupAuthoredGamesFetcher() {
        return dataFetchingEnvironment -> {
            CsldGroup group = dataFetchingEnvironment.getSource();
            return authoredGames.getGamesOfGroup(group, 0, 10000);
        };
    }

    public DataFetcher<Collection<IGameWithRating>> createUserPlayedGamesFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return gamesWithState.getPlayedByUser(user);
        };
    }

    public DataFetcher<Collection<Game>> createUserWantedGamesFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return gamesWithState.getWantedByUser(user);
        };
    }

    public DataFetcher<String[]> createGameAllowedActionsFetcher() {
        return dataFetchingEnvironment -> {
            Game game = dataFetchingEnvironment.getSource();

            return gameMutationFetcherFactory.hasGameEditAccess(game) ? ACCESS_EDIT_DELETE : null;
        };
    }
}
