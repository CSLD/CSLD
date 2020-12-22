package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class UserFetcherFactory {
    private final AppUsers appUsers;
    private final CsldUsers csldUsers;

    public UserFetcherFactory(AppUsers appUsers, CsldUsers csldUsers) {
        this.appUsers = appUsers;
        this.csldUsers = csldUsers;
    }

    public DataFetcher<CsldUser> createUserByIdFetcher() {
        return dataFetchingEnvironment -> csldUsers.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("userId")));
    }

    public DataFetcher<CsldUser> createUserByEmailFetcher() {
        return dataFetchingEnvironment -> csldUsers.getByEmail(dataFetchingEnvironment.getArgument("email"));
    }

    public DataFetcher<CsldUser> createLoggedInUserFetcher() {
        return dataFetchingEnvironment -> appUsers.getLoggedUser();
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
            CsldUser user = dataFetchingEnvironment.getSource();
            Person person = user.getPerson();
            String loggedInUserEmail = null;
            CsldUser loggedInUser = appUsers.getLoggedUser();
            if (loggedInUser != null) {
                loggedInUserEmail = loggedInUser.getPerson().getEmail();
            }

            // Return email only when user is editor or it is logged in person's email
            return (appUsers.isAtLeastEditor() || person.getEmail().equals(loggedInUserEmail)) ? person.getEmail() : "";
        };
    }

    public DataFetcher<String> createNameFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return user.getPerson().getName();
        };
    }

    public DataFetcher<String> createDescriptionFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return user.getPerson().getDescription();
        };
    }

    public DataFetcher<String> createNicknameFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return user.getPerson().getNickname();
        };
    }

    public DataFetcher<Date> createBirthDateFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return user.getPerson().getBirthDate();
        };
    }

    public DataFetcher<String> createCityFetcher() {
        return dataFetchingEnvironment -> {
            CsldUser user = dataFetchingEnvironment.getSource();
            return user.getPerson().getCity();
        };
    }
}
