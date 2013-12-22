package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.ForgotPassword;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;

/**
 *
 */
public class CsldSignInPanel extends SignInPanel {
    public CsldSignInPanel(String id) {
        super(id);

        getForm().add(new BookmarkablePageLink<CsldBasePage>("forgotPassword", ForgotPassword.class));
        getForm().add(new Button("submitButton", new ResourceModel("form.signIn", "Sign In")));
    }

    /**
     * @see org.apache.wicket.Component#onBeforeRender()
     */
    @Override
    protected void onBeforeRender()
    {
        // logged in already?
        if (!isSignedIn())
        {
            IAuthenticationStrategy authenticationStrategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();
            // get username and password from persistence store
            String[] data = authenticationStrategy.load();

            if ((data != null) && (data.length > 1))
            {
                // try to sign in the user
                if (signIn(data[0], data[1]))
                {
                    // logon successful. Continue to the original destination
                    continueToOriginalDestination();
                    // Ups, no original destination. Go to the home page
                    throw new RestartResponseException(getSession().getPageFactory().newPage(
                            getApplication().getHomePage()));
                }
                else
                {
                    // the loaded credentials are wrong. erase them.
                    authenticationStrategy.remove();
                }
            }
        }

        // don't forget
        super.onBeforeRender();
    }

    protected boolean signIn(String username, String password){
        return (CsldAuthenticatedWebSession.get()).signIn(username, password);
    }

    protected boolean isSignedIn(){
        return CsldAuthenticatedWebSession.get().isSignedIn();
    }
}
