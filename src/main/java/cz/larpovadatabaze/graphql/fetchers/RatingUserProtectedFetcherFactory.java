package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class RatingUserProtectedFetcherFactory {
    private final AppUsers appUsers;

    public RatingUserProtectedFetcherFactory(AppUsers appUsers) {
        this.appUsers = appUsers;
    }

    public DataFetcher<CsldUser> createRatingUserProtectedChecker() {
        return new DataFetcher<CsldUser>() {
            @Override
            public CsldUser get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                if (!appUsers.isAtLeastEditor()) {
                    throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Access denied to rating user");
                }

                Rating rating = dataFetchingEnvironment.getSource();
                return rating.getUser();
            }
        };
    }
}
