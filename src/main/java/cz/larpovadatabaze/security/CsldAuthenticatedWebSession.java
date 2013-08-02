package cz.larpovadatabaze.security;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.4.13
 * Time: 8:39
 */
public class CsldAuthenticatedWebSession extends AuthenticatedWebSession {
    private Roles actualRoles;
    private Integer loggedUserId;
    private CsldUser csldUser;

    @SpringBean
    private CsldUserService csldUserService;

    /**
     * Construct.
     *
     * @param request
     *            The current request object
     */
    public CsldAuthenticatedWebSession(Request request)
    {
        super(request);
        Injector.get().inject(this);
    }

    /**
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean authenticate(final String username, final String password)
    {
        CsldUser authenticated = csldUserService.authenticate(username, password);

        if(authenticated != null){
            if(authenticated.getRole() == 1) {
                actualRoles = new Roles(new String[]{
                        CsldRoles.USER.getRoleName()
                });
            } else if(authenticated.getRole() == 2) {
                actualRoles = new Roles(new String[]{
                        CsldRoles.USER.getRoleName(),
                        CsldRoles.EDITOR.getRoleName()
                });
            } else {
                actualRoles = new Roles(new String[]{
                        CsldRoles.USER.getRoleName(),
                        CsldRoles.ADMIN.getRoleName(),
                        CsldRoles.EDITOR.getRoleName()
                });
            }

            loggedUserId = authenticated.getId();
            csldUser = authenticated;
            return true;
        } else {
            return false;
        }
    }

    public CsldUser getLoggedUser() {
        return csldUser;
    }

    /**
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#getRoles()
     */
    @Override
    public Roles getRoles()
    {
        if (isSignedIn())
        {
            // If the user is signed in, they have these roles
            return actualRoles;
        }
        return null;
    }
}