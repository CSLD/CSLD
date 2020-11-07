package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fetches rating stats for the game
 */
public class GameRatingStatsFetcher implements DataFetcher<List<GameRatingStatsFetcher.RatingCount>> {
    public static class RatingCount {
        private final int rating;
        private final int count;

        private RatingCount(int rating, int count) {
            this.rating = rating;
            this.count = count;
        }
    }

    @Override
    public List<RatingCount> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Game game = dataFetchingEnvironment.getSource();

        Map<Integer, Long> map = game.getRatings().stream()
                .filter(rating -> rating.getRating() != null)
                .collect(Collectors.groupingBy(Rating::getRating, Collectors.counting()));
        List<RatingCount> res = map.entrySet().stream().map(entry -> new RatingCount(entry.getKey(), entry.getValue().intValue())).collect(Collectors.toList());
        return res;
    };
}
