package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.services.CsldUsers;
import cz.larpovadatabaze.services.EmailAuthentications;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.utils.RandomString;
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

        Form resetPassword = new Form("resetPassword"){
            @Override
            protected void onSubmit() {
                if(password != null && passwordAgain != null && password.equals(passwordAgain)){
                    csldUser.setPassword(Pwd.generateStrongPasswordHash(password, csldUser.getPerson().getEmail()));
                    csldUsers.saveOrUpdate(csldUser);

                    throw new RestartResponseException(HomePage.class);
                }
            }
        };

        resetPassword.add(new PasswordTextField("password", new PropertyModel<String>(this, "password")));
        resetPassword.add(new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain")));
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
