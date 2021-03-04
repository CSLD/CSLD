package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RatingFetcherFactory {
    private final Ratings ratings;
    private final AppUsers appUsers;

    @Autowired
    public RatingFetcherFactory(Ratings ratings, AppUsers appUsers) {
        this.ratings = ratings;
        this.appUsers = appUsers;
    }

    public DataFetcher<List<Rating>> createUserRatingsFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser source = dataFetchingEnvironment.getSource();
            CsldUser loggedUser = appUsers.getLoggedUser();

            return ratings.getRatingsOfUser(loggedUser, source);
        };
    }

    public DataFetcher<Rating> createUsersGameRatingFetcher() {
        return dataFetchingEnvironment -> {
            Integer loggedUserId = appUsers.getLoggedUserId();
            if (loggedUserId == null) {
                return null;
            }

            Game game = dataFetchingEnvironment.getSource();
            return ratings.getUserRatingOfGame(loggedUserId, game.getId());
        };
    }
}
