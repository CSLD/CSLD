package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.administration.model.MonthlyAmountsStatisticsDto;
import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.services.AdministeredUsers;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminQueryFetcherFactory {
    private final Statistics statistics;
    private final AdministeredUsers authors;
    private final Games games;
    private final CsldUsers users;

    private static class SelfRated {
        private final String id;
        private final Game game;
        private final CsldUser user;

        private SelfRated(Game game, CsldUser user) {
            this.id = user.getId().toString() + ":" + game.getId().toString();
            this.game = game;
            this.user = user;
        }
    }

    private static class StatFact {
        private final String id;
        private final int year;
        private final int month;
        private Integer numRatings;
        private Float averageRating;
        private Integer numComments;

        private StatFact(int year, int month) {
            this.year = year;
            this.month = month;
            this.id = String.format("%04d%02d", year, month);
        }

        public void setNumRatings(Integer numRatings) {
            this.numRatings = numRatings;
        }

        public void setAverageRating(Float averageRating) {
            this.averageRating = averageRating;
        }

        public void setNumComments(Integer numComments) {
            this.numComments = numComments;
        }
    }

    private static class StatMap {
        private Map<Integer, StatFact> map = new HashMap<>();
        
        private int minKey = 999999;

        /**
         * Get fact for given year and month from the map, create it when it does not exist.
         *
         * @param year Fact year
         * @param month Fact month
         *
         * @return Fact object
         */
        public StatFact getFact(Object year, Object month) {
            int iYear = 0;
            int iMonth = 0;
            if ((year instanceof Double) && (month instanceof Double)) {
                iYear = ((Double)year).intValue();
                iMonth = ((Double)month).intValue();
           } else {
                throw new IllegalArgumentException("Bad argument types");
            }

            Integer key = iYear*100 + iMonth;
            if (key < minKey) {
                minKey = key;
            }
            StatFact fact = map.get(key);
            if (fact == null) {
                fact = new StatFact(iYear, iMonth);
                map.put(key, fact);
            }
            return fact;
        }

        /**
         * Add to map records for months between earliest month and current month
         */
        public void fillInEmptyFacts() {
            GregorianCalendar current = new GregorianCalendar();

            int thisYear = current.get(Calendar.YEAR);
            int thisMonth = current.get(Calendar.MONTH)+1;

            int year = minKey / 100;
            int month = minKey % 100;

            while((year < thisYear) || ((year == thisYear) && (month <= thisMonth))) {
                Integer key = year*100 + month;
                if (!map.containsKey(key)) {
                    map.put(key, new StatFact(year, month));
                }
                month++;
                if (month == 13) {
                    month = 1;
                    year++;
                }
            }
        }

        /**
         * Get facts as a collection
         */
        public Collection<StatFact> asFacts() {
            return map.values();
        }
    }

    @Autowired
    public AdminQueryFetcherFactory(Statistics statistics, AdministeredUsers authors, Games games, CsldUsers users) {
        this.statistics = statistics;
        this.authors = authors;
        this.games = games;
        this.users = users;
    }

    public DataFetcher<Collection<StatFact>> createStatsFetcher() {
        return new DataFetcher<Collection<StatFact>>() {
            @Override
            public Collection<StatFact> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                StatMap map = new StatMap();

                for(RatingStatisticsDto rs : statistics.amountAndRatingsPerMonth()) {
                    StatFact fact = map.getFact(rs.getYear(), rs.getMonth());
                    fact.setAverageRating(((BigDecimal)rs.getAverageRating()).floatValue());
                    fact.setNumRatings(((BigInteger)rs.getNumRatings()).intValue());
                }

                for(MonthlyAmountsStatisticsDto cs : statistics.amountOfCommentsPerMonth()) {
                    StatFact fact = map.getFact(cs.year, cs.month);
                    fact.setNumComments(((BigInteger)cs.amount).intValue());
                }

                map.fillInEmptyFacts();
                return map.asFacts();
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
