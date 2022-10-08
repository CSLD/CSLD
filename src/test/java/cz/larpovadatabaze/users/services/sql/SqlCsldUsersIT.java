package cz.larpovadatabaze.users.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SqlCsldUsersIT extends WithDatabase {
    private CsldUsers underTest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        underTest = new SqlCsldUsers(sessionFactory,
                Mockito.mock(Images.class),
                Mockito.mock(MailService.class),
                Mockito.mock(EmailAuthentications.class),
                Mockito.mock(FileService.class),
                Mockito.mock(ImageResizingStrategyFactoryService.class),
                Mockito.mock(Ratings.class),
                Mockito.mock(Comments.class),
                Mockito.mock(Upvotes.class),
                Mockito.mock(Events.class),
                Mockito.mock(Labels.class),
                Mockito.mock(Photos.class),
                Mockito.mock(AppUsers.class),
                Mockito.mock(Games.class)
        );
    }

    @Test
    public void getValidEditors() {
        List<CsldUser> editors = underTest.getEditors();
        assertThat(editors.size(), is(1));
        assertThat(editors.get(0).getPerson().getName(), is("Editor"));
    }

    @Test
    public void getValidAdministrators() {
        List<CsldUser> administrators = underTest.getAdmins();
        assertThat(administrators.size(), is(1));
        assertThat(administrators.get(0).getPerson().getName(), is("Administrator"));
    }

    @Test
    public void authenticateAsExistingUserReturnTheUser() {
        String email = "user@masquerade.test";
        CsldUser loggedIn = underTest.authenticate(
                email,
                Pwd.generateStrongPasswordHash("user", email)
        );

        assertThat(loggedIn, is(notNullValue()));
        assertThat(loggedIn.getPerson().getName(), is("User"));
    }

    @Test
    public void authenticateAsNonexistentReturnNull() {
        CsldUser loggedIn = underTest.authenticate("nonexistent", "wrongPassword");

        assertThat(loggedIn, is(nullValue()));
    }

    @Test
    public void getByValidEmailReturnsValidUsers() {
        CsldUser validByEmail = underTest.getByEmail("user@masquerade.test");

        assertThat(validByEmail, is(notNullValue()));
        assertThat(validByEmail.getPerson().getName(), is("User"));
    }
}
