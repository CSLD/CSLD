package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.RandomString;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserFetcherFactory {
    private final CsldUsers csldUsers;
    private final EmailAuthentications emailAuthentications;
    private final AppUsers appUsers;

    @Autowired
    public UserFetcherFactory(CsldUsers csldUsers, EmailAuthentications emailAuthentications, AppUsers appUsers) {
        this.csldUsers = csldUsers;
        this.emailAuthentications = emailAuthentications;
        this.appUsers = appUsers;
    }

    public DataFetcher<CsldUser> createLoggedInUserFetcher() {
        return dataFetchingEnvironment -> appUsers.getLoggedUser();
    }

    public DataFetcher<CsldUser> createUserByIdFetcher() {
        return dataFetchingEnvironment -> csldUsers.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("id")));
    }

    public DataFetcher<CsldUser> createUserByEmailFetcher() {
        return dataFetchingEnvironment -> csldUsers.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("email")));
    }

    public DataFetcher<CsldUser> createLogInMutationFetcher() {
        return dataFetchingEnvironment -> {
            if (!appUsers.signIn(dataFetchingEnvironment.getArgument("userName"), dataFetchingEnvironment.getArgument("password"))) {
                // Login failed
                return null;
            }

            return appUsers.getLoggedUser();
        };
    }

    public DataFetcher<CsldUser> createLogOutMutationFetcher() {
        return dataFetchingEnvironment -> {
            appUsers.signOut();

            return appUsers.getLoggedUser();
        };
    }

    public DataFetcher<CsldUser> createCreateUserMutationFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            String email = (String) input.get("email");

            // Is user with this e-mail already present?
            if (csldUsers.getByEmail(email) != null) {
                throw new GraphQLException(GraphQLException.ErrorCode.DUPLICATE_VALUE, "User with this email already exists", "input.email");
            }

            CsldUser csldUser = new CsldUser();
            Person person = new Person();
            person.setEmail(email);
            // TODO - profile picture - TODO
            person.setName((String) input.get("name"));
            person.setNickname((String) input.get("nickname"));
            person.setBirthDate(FetcherUtils.parseDate((String) input.get("birthDate"), "input.birthDate"));
            person.setCity((String) input.get("city"));
            csldUser.setPerson(person);
            // TODO - check recaptcha - TODO
            csldUser.setPassword(Pwd.generateStrongPasswordHash((String) input.get("password"), csldUser.getPerson().getEmail()));


            // Create user
            csldUsers.saveOrUpdate(csldUser);

            // Log in as new user
            if (!appUsers.signIn((String) input.get("email"), (String) input.get("password"))) {
                // Login failed (should never happen here)
                return null;
            }
            return csldUser;
        };
    }

    public DataFetcher<CsldUser> createUpdateLoggedInUserMutationFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            if (!appUsers.isSignedIn()) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_STATE, "Not logged in");
            }

            CsldUser csldUser = appUsers.getLoggedUser();
            csldUser.getPerson().setEmail((String) input.get("email"));
            // TODO - profile picture - TODO
            csldUser.getPerson().setName((String) input.get("name"));
            csldUser.getPerson().setNickname((String) input.get("nickname"));
            csldUser.getPerson().setBirthDate(FetcherUtils.parseDate((String) input.get("birthDate"), "input.birthDate"));
            csldUser.getPerson().setCity((String) input.get("city"));

            csldUsers.saveOrUpdate(csldUser);

            return csldUser;
        };
    }

    public DataFetcher<CsldUser> createUpdateLoggedInUserPasswordMutationFetcher() {
        return dataFetchingEnvironment -> {
            String oldPassword = dataFetchingEnvironment.getArgument("oldPassword");
            String newPassword = dataFetchingEnvironment.getArgument("newPassword");

            if (!appUsers.isSignedIn()) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_STATE, "Not logged in");
            }

            CsldUser csldUser = appUsers.getLoggedUser();
            String password = csldUser.getPassword();
            if (password == null) {
                password = "";
            }
            if (!Pwd.validatePassword(oldPassword, password)) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Old password not valid", "oldPassword");
            }

            csldUser.setPassword(Pwd.generateStrongPasswordHash(newPassword, csldUser.getPerson().getEmail()));
            csldUsers.saveOrUpdate(csldUser);

            return csldUser;
        };
    }

    public DataFetcher<Boolean> createStartRecoverPasswordMutationFetcher() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email");
            String recoverUrl = dataFetchingEnvironment.getArgument("recoverUrl");

            EmailAuthentication emailAuthentication = new EmailAuthentication();
            CsldUser user = csldUsers.getByEmail(email);
            if (user == null) {
                // User does not exist
                return false;
            }
            emailAuthentication.setUser(user);

            String token = new RandomString(32).nextString();
            emailAuthentication.setAuthToken(token);

            csldUsers.sendForgottenPassword(user, emailAuthentication, recoverUrl + '/' + token);

            emailAuthentications.saveOrUpdate(emailAuthentication);

            return true;
        };
    }

    public DataFetcher<CsldUser> createFinishRecoverPasswordMutationFetcher() {
        return dataFetchingEnvironment -> {
            String token = dataFetchingEnvironment.getArgument("token");
            String newPassword = dataFetchingEnvironment.getArgument("newPassword");

            EmailAuthentication authentication = emailAuthentications.getByKey(token);
            if (authentication == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Token does not exist", "token");
            }

            // Set new password
            CsldUser csldUser = authentication.getUser();
            String email = csldUser.getPerson().getEmail();
            csldUser.setPassword(Pwd.generateStrongPasswordHash(newPassword, email));
            csldUsers.saveOrUpdate(csldUser);

            // Remove token info from DB - it is used up
            emailAuthentications.remove(authentication);

            // Log in user
            if (!appUsers.signIn(email, newPassword)) {
                // Login failed (should never happen here)
                return null;
            }

            return appUsers.getLoggedUser();
        };
    }
}
