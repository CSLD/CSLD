package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.graphql.GraphQLUploadedFile;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.RandomString;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import graphql.schema.DataFetcher;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authentication.strategy.NoOpAuthenticationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserFetcherFactory {
    private final CsldUsers csldUsers;
    private final EmailAuthentications emailAuthentications;
    private final AppUsers appUsers;

    // Initialized later manually to overcome circular reference during bean creation
    private WebApplication webApplication;

    @Autowired
    public UserFetcherFactory(CsldUsers csldUsers, EmailAuthentications emailAuthentications, AppUsers appUsers) {
        this.csldUsers = csldUsers;
        this.emailAuthentications = emailAuthentications;
        this.appUsers = appUsers;
    }

    public void setWebApplication(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    private IAuthenticationStrategy getAuthenticationStrategy() {
        if (webApplication == null) {
            // Used in tests
            return new NoOpAuthenticationStrategy();
        }

        return webApplication.getSecuritySettings().getAuthenticationStrategy();
    }

    public DataFetcher<CsldUser> createLoggedInUserFetcher() {
        return dataFetchingEnvironment -> appUsers.getLoggedUser();
    }

    public DataFetcher<CsldUser> createUserByIdFetcher() {
        return dataFetchingEnvironment -> csldUsers.getById(Integer.parseInt(dataFetchingEnvironment.getArgument("userId")));
    }

    public DataFetcher<CsldUser> createUserByEmailFetcher() {
        return dataFetchingEnvironment -> csldUsers.getByEmail(dataFetchingEnvironment.getArgument("email"));
    }

    public DataFetcher<CsldUser> createLogInMutationFetcher() {
        return dataFetchingEnvironment -> {
            String userName = dataFetchingEnvironment.getArgument("userName");
            String password = dataFetchingEnvironment.getArgument("password");
            if (!appUsers.signIn(userName, password)) {
                // Login failed
                return null;
            }

            // Remember login
            getAuthenticationStrategy().save(userName, password);

            return appUsers.getLoggedUser();
        };
    }

    public DataFetcher<CsldUser> createLogOutMutationFetcher() {
        return dataFetchingEnvironment -> {
            appUsers.signOut();

            getAuthenticationStrategy().remove();

            return appUsers.getLoggedUser();
        };
    }

    public DataFetcher<CsldUser> createCreateUserMutationFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            String email = (String) input.get("email");
            String password = (String) input.get("password");

            // Is user with this e-mail already present?
            if (csldUsers.getByEmail(email) != null) {
                throw new GraphQLException(GraphQLException.ErrorCode.DUPLICATE_VALUE, "User with this email already exists", "input.email");
            }

            // Check recaptcha (remote IP we see is from the CORS proxy, so we cannot use it)
            if (!csldUsers.checkReCaptcha((String)input.get("recaptcha"), null)) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Recaptcha token invalid", "input.recaptcha");
            }

            CsldUser csldUser = new CsldUser();
            Person person = new Person();
            person.setEmail(email);
            person.setName((String) input.get("name"));
            person.setNickname((String) input.get("nickname"));
            person.setBirthDate(FetcherUtils.parseDate((String) input.get("birthDate"), "input.birthDate"));
            person.setCity((String) input.get("city"));
            csldUser.setPerson(person);
            // Password will be encoded during saveOrUpdate()
            csldUser.setPassword(password);

            // Image
            GraphQLUploadedFile profilePicture = null;
            Map<String, String> profilePictureMap = (Map<String, String>)input.get("profilePicture");
            if (profilePictureMap != null) {
                profilePicture = new GraphQLUploadedFile(profilePictureMap.get("fileName"), profilePictureMap.get("contents"));
            }

            // Create user
            csldUsers.saveOrUpdate(csldUser, null, profilePicture);

            // Log in as new user
            if (!appUsers.signIn(email, password)) {
                // Login failed (should never happen here)
                return null;
            }

            // Remember login
            getAuthenticationStrategy().save(email, password);

            return csldUser;
        };
    }

    public DataFetcher<CsldUser> createUpdateLoggedInUserMutationFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            if (!appUsers.isSignedIn()) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_STATE, "Not logged in");
            }

            CsldUser currentUser = appUsers.getLoggedUser();

            CsldUser newUser = new CsldUser();
            newUser.setId(currentUser.getId());
            Person person = new Person();
            person.setEmail((String) input.get("email"));
            person.setName((String) input.get("name"));
            person.setNickname((String) input.get("nickname"));
            person.setBirthDate(FetcherUtils.parseDate((String) input.get("birthDate"), "input.birthDate"));
            person.setCity((String) input.get("city"));
            newUser.setPerson(person);

            // Image
            GraphQLUploadedFile profilePicture = null;
            Map<String, String> profilePictureMap = (Map<String, String>)input.get("profilePicture");
            if (profilePictureMap != null) {
                profilePicture = new GraphQLUploadedFile(profilePictureMap.get("fileName"), profilePictureMap.get("contents"));
            }

            csldUsers.saveOrUpdate(newUser, null, profilePicture);

            return newUser;
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

            // Remember login
            getAuthenticationStrategy().save(email, newPassword);

            return appUsers.getLoggedUser();
        };
    }
}
