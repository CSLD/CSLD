package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.dao.CsldUserDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUsers;
import cz.larpovadatabaze.services.Images;
import cz.larpovadatabaze.utils.Pwd;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SqlCsldUsersIT extends WithDatabase {
    private CsldUsers underTest;

    @Before
    public void setUp() {
        super.setUp();

        Images images = Mockito.mock(Images.class);
        CsldUserDAO dao = new CsldUserDAO(sessionFactory);
        underTest = new SqlCsldUsers(dao, images);
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
