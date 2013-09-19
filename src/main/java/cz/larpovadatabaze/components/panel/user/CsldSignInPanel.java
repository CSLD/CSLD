package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.utils.Pwd;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;

/**
 *
 */
public class CsldSignInPanel extends SignInPanel {
    private String username;
    private String password;

    public CsldSignInPanel(String id) {
        super(id);
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
                    username = data[0];
                    password = data[1];

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
        return (CsldAuthenticatedWebSession.get()).signIn(username, getPassword());
    }

    protected boolean isSignedIn(){
        return CsldAuthenticatedWebSession.get().isSignedIn();
    }

    @Override
    public String getPassword() {
        return Pwd.getMD5(super.getPassword());
    }
}
