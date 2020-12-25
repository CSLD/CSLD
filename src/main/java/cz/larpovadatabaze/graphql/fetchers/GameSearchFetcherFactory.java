package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import cz.larpovadatabaze.games.services.FilteredGames;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.graphql.EntitySearchableCache;
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

@Component
public class GameSearchFetcherFactory {
    private final Games games;
    private final Labels labels;
    private final FilteredGames filteredGames;
    private final GameSearchableCache gameSearchableCache = new GameSearchableCache();

    private class GameSearchableCache extends EntitySearchableCache<Game> {
        @Override
        public Collection<Game> getAll() {
            return games.getAll();
        }
    }


    private static class GamesPaged {
        private final List<Game> games;
        private final long totalAmount;

        private GamesPaged(List<Game> games, long totalAmount) {
            this.games = games;
            this.totalAmount = totalAmount;
        }
    }

    @Autowired
    public GameSearchFetcherFactory(Games games, Labels labels, FilteredGames filteredGames) {
        this.games = games;
        this.labels = labels;
        this.filteredGames = filteredGames;
    }

    private GamesPaged getGames(DataFetchingEnvironment dataFetchingEnvironment, FilterGameDTO.OrderBy orderBy, boolean onlyNew) {
        FilterGameDTO filter = new FilterGameDTO(orderBy);
        filter.setShowOnlyNew(onlyNew);
        filter.setRequiredLabels(dataFetchingEnvironment.getArgumentOrDefault("requiredLabels", Collections.emptyList()));
        filter.setOtherLabels(dataFetchingEnvironment.getArgumentOrDefault("otherLabels", Collections.emptyList()));

        int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
        int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 10);

        long totalAmount = filteredGames.totalAmount(filter);
        List<Game> gameList = filteredGames.paginated(filter, offset, limit);

        return new GamesPaged(gameList, totalAmount);
    }

    public DataFetcher<List<Game>> createByQueryFetcher() {
        return dataFetchingEnvironment -> {
            int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
            int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);
            String query = dataFetchingEnvironment.getArgument("query");

            return gameSearchableCache.search(query, offset, limit);
        };
    }

    public DataFetcher<GamesPaged> createRecentAndMostPlayedFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getGames(dataFetchingEnvironment, FilterGameDTO.OrderBy.NUM_RATINGS_DESC, true);
            }
        };
    }

    public DataFetcher<GamesPaged> createMostPlayedFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getGames(dataFetchingEnvironment, FilterGameDTO.OrderBy.ADDED_DESC, true);
            }
        };
    }

    public DataFetcher<GamesPaged> createRecentFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getGames(dataFetchingEnvironment, FilterGameDTO.OrderBy.RATING_DESC, false);
            }
        };
    }

    public DataFetcher<GamesPaged> createBestFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getGames(dataFetchingEnvironment, FilterGameDTO.OrderBy.NUM_RATINGS_DESC, false);
            }
        };
    }

    public DataFetcher<GamesPaged> createMostCommentedFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getGames(dataFetchingEnvironment, FilterGameDTO.OrderBy.NUM_COMMENTS_DESC, false);
            }
        };
    }
}
