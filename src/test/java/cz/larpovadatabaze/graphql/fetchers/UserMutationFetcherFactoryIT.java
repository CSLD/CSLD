package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.entities.Person;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.masquerade.InMemoryCsldUsers;
import cz.larpovadatabaze.users.services.masquerade.InMemoryEmailAuthenticationTokens;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class UserMutationFetcherFactoryIT {
    @Test
    public void createUserHappyDay() throws Exception {
        AppUsers sessionMock = mock(AppUsers.class);
        when(sessionMock.signIn(anyString(), anyString())).thenReturn(true);
        InMemoryCsldUsers users = new InMemoryCsldUsers();
        users.removeAll();

        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(users, new InMemoryEmailAuthenticationTokens(), sessionMock);

        DataFetcher<CsldUser> fetcher = factory.createCreateUserMutationFetcher();
        Map<String, Object> input = new HashMap<>();
        input.put("email", "aaa@bbb.cz");
        input.put("name", "Petr Peta");
        input.put("nickname", "Petrous");
        input.put("birthDate", "1980-06-01");
        input.put("city", "Praha");
        input.put("password", "tajneheslo");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", input);

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        CsldUser created = fetcher.get(dataFetchingEnvironment);

        assertThat(created, notNullValue());
        assertThat(created.getPerson().getName(), equalTo("Petr Peta"));
        verify(sessionMock, times(1)).signIn("aaa@bbb.cz", "tajneheslo");

        // Check created record
        List<CsldUser> userList = users.getAll();
        assertThat(userList.size(), equalTo(1));
        CsldUser createdUser = userList.get(0);
        assertThat(createdUser.getPerson().getName(), equalTo("Petr Peta"));
        assertThat(createdUser.getPerson().getEmail(), equalTo("aaa@bbb.cz"));
        assertThat(createdUser.getPerson().getNickname(), equalTo("Petrous"));
        assertThat(FetcherUtils.formatDate(createdUser.getPerson().getBirthDate()), equalTo("1980-06-01"));
        assertThat(createdUser.getPerson().getCity(), equalTo("Praha"));
    }

    @Test
    public void createUserInvalidDate() {
        AppUsers sessionMock = mock(AppUsers.class);
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), new InMemoryEmailAuthenticationTokens(), sessionMock);

        DataFetcher<CsldUser> fetcher = factory.createCreateUserMutationFetcher();
        Map<String, Object> input = new HashMap<>();
        input.put("email", "aaa@bbb.cz");
        input.put("name", "Petr Peta");
        input.put("nickname", "Petrous");
        input.put("birthDate", "1980-XX-01");
        input.put("city", "Praha");
        input.put("password", "tajneheslo");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", input);

        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);
        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.INVALID_VALUE));
            assertThat(ex.getErrorPath(), equalTo("input.birthDate"));
        }
    }

    @Test
    public void updateUserHappyDay() throws Exception {
        CsldUser user = new CsldUser();
        user.setId(9);
        user.setPerson(new Person());
        AppUsers sessionMock = mock(AppUsers.class);
        when(sessionMock.getLoggedUser()).thenReturn(user);
        when(sessionMock.isSignedIn()).thenReturn(true);

        Map<String, Object> input = new HashMap<>();
        input.put("email", "aaa@bbb.cz");
        input.put("name", "Petr Peta");
        input.put("nickname", "Petrous");
        input.put("birthDate", "1980-09-01");
        input.put("city", "Praha");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", input);

        InMemoryCsldUsers users = new InMemoryCsldUsers();
        users.removeAll();
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(users, new InMemoryEmailAuthenticationTokens(), sessionMock);
        DataFetcher<CsldUser> fetcher = factory.createUpdateLoggedInUserMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        fetcher.get(dataFetchingEnvironment);

        List<CsldUser> userList = users.getAll();
        assertThat(userList.size(), equalTo(1));
        CsldUser updatedUser = userList.get(0);
        assertThat(updatedUser.getPerson().getName(), equalTo("Petr Peta"));
        assertThat(updatedUser.getPerson().getEmail(), equalTo("aaa@bbb.cz"));
        assertThat(updatedUser.getPerson().getNickname(), equalTo("Petrous"));
        assertThat(FetcherUtils.formatDate(updatedUser.getPerson().getBirthDate()), equalTo("1980-09-01"));
        assertThat(updatedUser.getPerson().getCity(), equalTo("Praha"));
    }

    @Test
    public void updateUserNotLoggedIn() {
        AppUsers sessionMock = mock(AppUsers.class);
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), new InMemoryEmailAuthenticationTokens(), sessionMock);
        DataFetcher<CsldUser> fetcher = factory.createUpdateLoggedInUserMutationFetcher();
        Map<String, Object> arguments = new HashMap<>();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.INVALID_STATE));
        }
    }

    @Test
    public void updatePasswordUserNotLoggedIn() {
        AppUsers sessionMock = mock(AppUsers.class);
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), new InMemoryEmailAuthenticationTokens(), sessionMock);
        DataFetcher<CsldUser> fetcher = factory.createUpdateLoggedInUserPasswordMutationFetcher();
        Map<String, Object> arguments = new HashMap<>();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.INVALID_STATE));
        }
    }

    @Test
    public void updatePasswordBadOldPassword() {
        CsldUser user = new CsldUser();
        user.setId(9);
        user.setPerson(new Person());
        AppUsers sessionMock = mock(AppUsers.class);
        when(sessionMock.getLoggedUser()).thenReturn(user);
        when(sessionMock.isSignedIn()).thenReturn(true);

        Map<String, Object> input = new HashMap<>();
        input.put("oldPassword", "old");
        input.put("newPassword", "new");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("input", input);

        InMemoryCsldUsers users = new InMemoryCsldUsers();
        users.removeAll();
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(users, new InMemoryEmailAuthenticationTokens(), sessionMock);
        DataFetcher<CsldUser> fetcher = factory.createUpdateLoggedInUserPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.INVALID_VALUE));
            assertThat(ex.getErrorPath(), equalTo("oldPassword"));
        }
    }

    @Test
    public void updatePasswordHappyDay() throws Exception {
        CsldUser user = new CsldUser();
        user.setId(9);
        user.setPerson(new Person());
        user.getPerson().setEmail("aaa@bbb.cz");
        user.setPassword(Pwd.generateStrongPasswordHash("old", user.getPerson().getEmail()));
        AppUsers sessionMock = mock(AppUsers.class);
        when(sessionMock.getLoggedUser()).thenReturn(user);
        when(sessionMock.isSignedIn()).thenReturn(true);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("oldPassword", "old");
        arguments.put("newPassword", "new");

        InMemoryCsldUsers users = new InMemoryCsldUsers();
        users.removeAll();
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(users, new InMemoryEmailAuthenticationTokens(), sessionMock);
        DataFetcher<CsldUser> fetcher = factory.createUpdateLoggedInUserPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        fetcher.get(dataFetchingEnvironment);
    }

    @Test
    public void recoverPasswordHappyDay() throws Exception {
        AppUsers sessionMock = mock(AppUsers.class);
        when(sessionMock.signIn(anyString(), anyString())).thenReturn(true);

        Map<String, Object> arguments1 = new HashMap<>();
        arguments1.put("email", "administrator@masquerade.test");
        arguments1.put("recoverUrl", "https://www.centrum.cz");

        InMemoryEmailAuthenticationTokens tokens = new InMemoryEmailAuthenticationTokens();
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), tokens, sessionMock);

        // Create token
        DataFetcher<Boolean> fetcher1 = factory.createStartRecoverPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment1 = new MockDataFetchingEnvironment(arguments1, null);

        boolean result = fetcher1.get(dataFetchingEnvironment1);
        assertThat(result, equalTo(true));

        List<EmailAuthentication> emails = tokens.getAll();
        assertThat(emails.size(), equalTo(1));

        Map<String, Object> arguments2 = new HashMap<>();
        arguments2.put("token", emails.get(0).getAuthToken());
        arguments2.put("newPassword", "new");

        // Update password
        DataFetcher<CsldUser> fetcher2 = factory.createFinishRecoverPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment2 = new MockDataFetchingEnvironment(arguments2, null);

        fetcher2.get(dataFetchingEnvironment2);

        assertThat(tokens.getAll().size(), equalTo(0));
    }

    @Test
    public void startPasswordRecoveryInvalidEmail() throws Exception {
        Map<String, Object> arguments1 = new HashMap<>();
        arguments1.put("email", "aaa@bbb.cz");
        arguments1.put("recoverUrl", "https://www.centrum.cz");

        InMemoryEmailAuthenticationTokens tokens = new InMemoryEmailAuthenticationTokens();
        AppUsers sessionMock = mock(AppUsers.class);
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), tokens, sessionMock);

        // Create token
        DataFetcher<Boolean> fetcher = factory.createStartRecoverPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments1, null);

        fetcher.get(dataFetchingEnvironment);

        assertThat(tokens.getAll().size(), equalTo(0));
    }

    @Test
    public void finishPasswordRecoveryInvalidToken() throws Exception {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("token", "zzzzz");
        arguments.put("newPassword", "new");

        AppUsers sessionMock = mock(AppUsers.class);
        UserMutationFetcherFactory factory = new UserMutationFetcherFactory(new InMemoryCsldUsers(), new InMemoryEmailAuthenticationTokens(), sessionMock);

        // Create token
        DataFetcher<CsldUser> fetcher = factory.createFinishRecoverPasswordMutationFetcher();
        DataFetchingEnvironment dataFetchingEnvironment = new MockDataFetchingEnvironment(arguments, null);

        try {
            fetcher.get(dataFetchingEnvironment);
            // Should have thrown exception
            assertThat(1, equalTo(2));
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(GraphQLException.class));

            GraphQLException ex = (GraphQLException) e;
            assertThat(ex.getCode(), equalTo(GraphQLException.ErrorCode.NOT_FOUND));
            assertThat(ex.getErrorPath(), equalTo("token"));
        }
    }
}
