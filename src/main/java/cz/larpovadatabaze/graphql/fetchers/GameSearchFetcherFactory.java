package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import cz.larpovadatabaze.games.services.FilteredGames;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
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

@Component
public class GameSearchFetcherFactory {
    private final Games games;
    private final Labels labels;
    private final FilteredGames filteredGames;

    private final static long CACHE_TTL = 60000; // 1 hour
    private long cacheExpiration = 0;
    private List<GameWithName> cachedGames;

    private static class GameWithName {
        private final String name;
        private final Game game;

        private GameWithName(String name, Game game) {
            this.name = name;
            this.game = game;
        }
    }

    private static String normalize(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.toLowerCase();
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

    private List<Label> getLabels(List<Object> ids) {
        if ((ids == null) || (ids.size() == 0)) {
            return null;
        }

        return ids.stream().map(id -> {
            Label label = labels.getById(Integer.parseInt((String)id));
            if (label == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Label not found");
            }
            return label;
        }).collect(Collectors.toList());
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

    public DataFetcher<List<Game>> createBySearchTermFetcher() {
        return new DataFetcher<List<Game>>() {
            @Override
            public List<Game> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                if (cacheExpiration < new Date().getTime()) {
                    // Re-fetch games
                    cachedGames = games.getAll().stream().map(game -> new GameWithName(normalize(game.getName()), game)).collect(Collectors.toList());
                    cacheExpiration = new Date().getTime() + CACHE_TTL;
                }

                String searchTerm = normalize(dataFetchingEnvironment.getArgument("searchTerm"));
                int offset = dataFetchingEnvironment.getArgumentOrDefault("offset", 0);
                int limit = dataFetchingEnvironment.getArgumentOrDefault("limit", 6);
                int needLen = offset + limit;

                List<Game> res = new ArrayList<>();
                for (GameWithName game : cachedGames) {
                    if (game.name.contains(searchTerm)) {
                        res.add(game.game);
                        if (res.size() >= needLen) {
                            break;
                        }
                    }
                }

                if (res.size() < offset) {
                    return Collections.emptyList();
                }

                return res.subList(offset, res.size());
            }
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