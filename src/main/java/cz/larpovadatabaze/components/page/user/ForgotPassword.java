package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.services.CsldUsers;
import cz.larpovadatabaze.services.EmailAuthentications;
import cz.larpovadatabaze.services.wicket.MailClient;
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
    EmailAuthentications emailAuthentications;
    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    MailClient mailClient;

    @SuppressWarnings("UnusedDeclaration")
    private String mail;

    public ForgotPassword(){
        Form forgotPassword = new Form("forgotPassword"){
            @Override
            protected void onSubmit() {
                EmailAuthentication emailAuthentication = new EmailAuthentication();
                CsldUser user = csldUsers.getByEmail(mail);
                if(user == null) {
                    error("Uživatel s tímto emailem neexistuje");
                    return;
                }
                emailAuthentication.setUser(user);

                String key = ResetPassword.nextUrl();
                PageParameters params = new PageParameters();
                params.add("0", key);
                emailAuthentication.setAuthToken(key);

                String mailBody = String.format("Pro vytvoření nového hesla použijte následující odkaz: %s", RequestCycle.get().getUrlRenderer().renderFullUrl(RequestCycle.get().mapUrlFor(ResetPassword.class, params)));
                mailClient.sendMail(mailBody, mail);
                emailAuthentications.saveOrUpdate(emailAuthentication);

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
