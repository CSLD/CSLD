package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.RandomString;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
public class ResetPassword extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    EmailAuthentications emailAuthentications;

    private String password;
    private String passwordAgain;
    private CsldUser csldUser;

    public ResetPassword(PageParameters params) {
        String key = params.get("0").toString();
        EmailAuthentication authentication = emailAuthentications.getByKey(key);
        if(authentication == null) {
            throw new RestartResponseException(HomePage.class);
        }
        csldUser = authentication.getUser();
        emailAuthentications.remove(authentication);

        Form<Void> resetPassword = new Form<>("resetPassword") {
            @Override
            protected void onSubmit() {
                if (password != null && passwordAgain != null && password.equals(passwordAgain)) {
                    csldUser.setPassword(Pwd.generateStrongPasswordHash(password, csldUser.getPerson().getEmail()));
                    csldUsers.saveOrUpdate(csldUser);

                    throw new RestartResponseException(HomePage.class);
                }
            }
        };

        resetPassword.add(new PasswordTextField("password", new PropertyModel<>(this, "password")));
        resetPassword.add(new PasswordTextField("passwordAgain", new PropertyModel<>(this, "passwordAgain")));
        resetPassword.add(new Button("submit"));

        add(resetPassword);
    }

    // Key must be stored in the database and allows you to change password

    public static String nextUrl() {
        String next = new RandomString(20).nextString();
        String encodedValue = org.apache.wicket.util.crypt.Base64.encodeBase64URLSafeString(next.getBytes());

        return encodedValue;
    }
}
