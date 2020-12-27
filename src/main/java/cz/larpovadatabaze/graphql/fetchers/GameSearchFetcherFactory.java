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

    private static class LadderConfig {
        private final FilterGameDTO.OrderBy orderBy;
        private final boolean onlyNew;

        private LadderConfig(FilterGameDTO.OrderBy orderBy, boolean onlyNew) {
            this.orderBy = orderBy;
            this.onlyNew = onlyNew;
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

    public DataFetcher<List<Game>> createByQueryFetcher() {
        return dataFetchingEnvironment -> {
            int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
            int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);
            String query = dataFetchingEnvironment.getArgument("query");

            return gameSearchableCache.search(query, offset, limit);
        };
    }

    private LadderConfig getLadderConfig(String ladderName) {
        if (ladderName != null) {
            switch (ladderName) {
                case "RecentAndMostPlayed":
                    return new LadderConfig(FilterGameDTO.OrderBy.NUM_RATINGS_DESC, true);
                case "MostPlayed":
                    return new LadderConfig(FilterGameDTO.OrderBy.NUM_RATINGS_DESC, false);
                case "Recent":
                    return new LadderConfig(FilterGameDTO.OrderBy.ADDED_DESC, true);
                case "Best":
                    return new LadderConfig(FilterGameDTO.OrderBy.RATING_DESC, false);
                case "MostCommented":
                    return new LadderConfig(FilterGameDTO.OrderBy.NUM_COMMENTS_DESC, false);
            }
        }

        throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Invalid ladder name '" + ladderName + "'", "ladder");
    }

    private List<Label> getLabels(List<String> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }

        return ids.stream().map(id -> labels.getById(Integer.parseInt(id))).collect(Collectors.toList());
    }

    public DataFetcher<GamesPaged> createLadderFetcher() {
        return new DataFetcher<GamesPaged>() {
            @Override
            public GamesPaged get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                LadderConfig ladderConfig = getLadderConfig(dataFetchingEnvironment.getArgument("ladderType"));

                FilterGameDTO filter = new FilterGameDTO(ladderConfig.orderBy);
                filter.setShowOnlyNew(ladderConfig.onlyNew);
                filter.setRequiredLabels(getLabels(dataFetchingEnvironment.getArgument("requiredLabels")));
                filter.setOtherLabels(getLabels(dataFetchingEnvironment.getArgument("otherLabels")));

                int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
                int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 10);

                long totalAmount = filteredGames.totalAmount(filter);
                List<Game> gameList = filteredGames.paginated(filter, offset, limit);

                return new GamesPaged(gameList, totalAmount);
            }
        };
    }
}
