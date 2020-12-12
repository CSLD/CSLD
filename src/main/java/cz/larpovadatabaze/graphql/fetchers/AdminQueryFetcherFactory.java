package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.administration.model.MonthlyAmountsStatisticsDto;
import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.model.UserRatesOwnGameDto;
import cz.larpovadatabaze.administration.services.AdministeredUsers;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminQueryFetcherFactory {
    private final Statistics statistics;
    private final AdministeredUsers authors;
    private final Games games;
    private final CsldUsers users;

    private static final class SelfRated {
        private final Game game;
        private final CsldUser user;

        private SelfRated(Game game, CsldUser user) {
            this.game = game;
            this.user = user;
        }
    }

    @Autowired
    public AdminQueryFetcherFactory(Statistics statistics, AdministeredUsers authors, Games games, CsldUsers users) {
        this.statistics = statistics;
        this.authors = authors;
        this.games = games;
        this.users = users;
    }

    public DataFetcher<List<RatingStatisticsDto>> createRatingStatsFetcher() {
        return new DataFetcher<List<RatingStatisticsDto>>() {
            @Override
            public List<RatingStatisticsDto> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return statistics.amountAndRatingsPerMonth();
            }
        };
    }

    public DataFetcher<List<MonthlyAmountsStatisticsDto>> createCommentStatsFetcher() {
        return new DataFetcher<List<MonthlyAmountsStatisticsDto>>() {
            @Override
            public List<MonthlyAmountsStatisticsDto> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return statistics.amountOfCommentsPerMonth();
            }
        };
    }

    public DataFetcher<List<SelfRated>> createSelfRatedFetcher() {
        return new DataFetcher<List<SelfRated>>() {
            @Override
            public List<SelfRated> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return authors.getUsersWhoRatesOwnGames().stream().map(entry -> new SelfRated(games.getById(entry.gameId), users.getById(entry.userId))).collect(Collectors.toList());
            }
        };
    }

    public DataFetcher<List<CsldUser>> createAllUsersFetcher() {
        return new DataFetcher<List<CsldUser>>() {
            @Override
            public List<CsldUser> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return users.getAll();
            }
        };
    }
}
