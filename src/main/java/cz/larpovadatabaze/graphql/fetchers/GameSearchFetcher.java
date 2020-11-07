package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Games;
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
public class GameSearchFetcher implements DataFetcher<List<Game>> {
    private final Games games;

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

    @Autowired
    public GameSearchFetcher(Games games) {
        this.games = games;
    }

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
}
