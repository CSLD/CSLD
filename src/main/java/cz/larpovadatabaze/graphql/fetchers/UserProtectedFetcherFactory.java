package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class UserProtectedFetcherFactory {
    private final AppUsers appUsers;

    public UserProtectedFetcherFactory(AppUsers appUsers) {
        this.appUsers = appUsers;
    }

    public DataFetcher<CsldUser> createRatingUserProtectedChecker() {
        return dataFetchingEnvironment -> {
            if (!appUsers.isAtLeastEditor()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Access denied to rating user");
            }

            Rating rating = dataFetchingEnvironment.getSource();
            return rating.getUser();
        };
    }

    public DataFetcher<String> createUserEmailProtectedChecker() {
        return dataFetchingEnvironment -> {
            Person person = dataFetchingEnvironment.getSource();
            String loggedInUserEmail = null;
            CsldUser loggedInUser = appUsers.getLoggedUser();
            if (loggedInUser != null) {
                loggedInUserEmail = loggedInUser.getPerson().getEmail();
            }

            // Return email only when user is editor or it is logged in person's email
            return (appUsers.isAtLeastEditor() || person.getEmail().equals(loggedInUserEmail)) ? person.getEmail() : "";
        };
    }
}
