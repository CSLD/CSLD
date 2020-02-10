package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
public class ForgotPassword extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;

    @SuppressWarnings("UnusedDeclaration")
    private String mail;

    public ForgotPassword(){
        Form<Void> forgotPassword = new Form<>("forgotPassword") {
            @Override
            protected void onSubmit() {

                EmailAuthentication emailAuthentication = new EmailAuthentication();
                CsldUser user = csldUsers.getByEmail(mail);
                if (user == null) {
                    error("Uživatel s tímto emailem neexistuje");
                    return;
                }
                emailAuthentication.setUser(user);

                String key = ResetPassword.nextUrl();
                PageParameters params = new PageParameters();
                params.add("0", key);
                emailAuthentication.setAuthToken(key);
                String url = RequestCycle.get().getUrlRenderer()
                        .renderFullUrl(RequestCycle.get().mapUrlFor(ResetPassword.class, params));
                if (url.startsWith("http://")) {
                    url = url.replace("http://", "https://");
                }

                csldUsers.sendForgottenPassword(user, emailAuthentication, url);

                throw new RestartResponseException(HomePage.class);
            }
        };
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        forgotPassword.add(feedback);

        forgotPassword.add(new EmailTextField("mail", new PropertyModel<String>(this,"mail")));
        forgotPassword.add(new Button("submit"));

        add(forgotPassword);
    }
}
