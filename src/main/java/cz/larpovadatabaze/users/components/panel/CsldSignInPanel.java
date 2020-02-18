package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.page.ForgotPassword;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.facebook.FacebookPermission;
import org.wicketstuff.facebook.behaviors.AuthLoginEventBehavior;
import org.wicketstuff.facebook.plugins.LoginButton;

/**
 *
 */
public class CsldSignInPanel extends SignInPanel {
    public CsldSignInPanel(String id) {
        super(id);

        add(new LoginButton("loginButton", FacebookPermission.email));
        final Model<String> responseModel = new Model<String>();
        final MultiLineLabel responseLabel = new MultiLineLabel("response", responseModel);
        responseLabel.setOutputMarkupId(true);
        add(responseLabel);


        add(new AuthLoginEventBehavior() {

            @Override
            protected void onSessionEvent(final AjaxRequestTarget target, final String status,
                                          final String userId, final String signedRequest, final String expiresIn,
                                          final String accessToken) {
                final StringBuilder sb = new StringBuilder();
                sb.append("status: ").append(status).append('\n');
                sb.append("signedRequest: ").append(signedRequest).append('\n');
                sb.append("expiresIn: ").append(expiresIn).append('\n');
                sb.append("accessToken: ").append(accessToken).append('\n');

                responseModel.setObject(sb.toString());

                target.add(responseLabel);
            }
        });

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
