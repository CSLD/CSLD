package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.EmailAuthenticationService;
import cz.larpovadatabaze.utils.MailClient;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.mail.MessagingException;

/**
 *
 */
public class ForgotPassword extends CsldBasePage {
    @SpringBean
    EmailAuthenticationService emailAuthenticationService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    private MailClient mailClient;

    private String mail;

    public ForgotPassword(){
        Form forgotPassword = new Form("forgotPassword"){
            @Override
            protected void onSubmit() {
                EmailAuthentication emailAuthentication = new EmailAuthentication();
                CsldUser user = csldUserService.getByEmail(mail);
                if(user == null) {
                    error("Uživatel s tímto emailem neexistuje");
                    return;
                }
                emailAuthentication.setUserId(user.getId());

                String key = ResetPassword.nextUrl();
                PageParameters params = new PageParameters();
                params.add("0", key);
                emailAuthentication.setAuthToken(key);

                String mailBody = String.format("Pro vytvoření nového hesla použijte následující odkaz: %s", RequestCycle.get().getUrlRenderer().renderFullUrl(RequestCycle.get().mapUrlFor(ResetPassword.class, params)));
                mailClient.sendMail(mailBody, mail);
                emailAuthenticationService.saveOrUpdate(emailAuthentication);

                new RestartResponseException(HomePage.class);
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
