package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.page.ForgotPassword;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.facebook.FacebookPermission;
import org.wicketstuff.facebook.FacebookSdk;
import org.wicketstuff.facebook.behaviors.AuthStatusChangeEventBehavior;
import org.wicketstuff.facebook.plugins.LoginButton;

/**
 *
 */
public class CsldSignInPanel extends SignInPanel {
    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    AppUsers appUsers;

    public CsldSignInPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new FacebookSdk("fbRoot", "589271441623477"));
        add(new LoginButton("loginButton", FacebookPermission.email));
        final Model<String> responseModel = new Model<>();
        final MultiLineLabel responseLabel = new MultiLineLabel("response", responseModel);
        responseLabel.setOutputMarkupId(true);
        add(responseLabel);

        getForm().add(new BookmarkablePageLink<CsldBasePage>("forgotPassword", ForgotPassword.class));
        getForm().add(new Button("submitButton", new ResourceModel("form.signIn", "Sign In")));


        // Once FB is loaded. Not earlier.
        add(new AuthStatusChangeEventBehavior() {
            @Override
            protected void onSessionEvent(final AjaxRequestTarget target, final String status,
                                          final String userId, final String signedRequest, final String expiresIn,
                                          final String accessToken) {
                if (status.equals("connected")) {
                    CsldUser connectedUser = csldUsers.byFbId(userId);
                    // If a User is logged in, just add info to the logged user.
                    if (connectedUser != null) {
                        CsldAuthenticatedWebSession.get().setLoggedUser(connectedUser);
                    } else {
                        if (CsldAuthenticatedWebSession.get().isSignedIn()) {
                            // Update current user by adding the FB Id
                        } else {
                            // Create new user available only via FB Id until the details are provided elsewhere.
                        }
                    }
                } else {
                    responseModel.setObject("Invalid Login");
                }

                target.add(responseLabel);
            }
        });
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
