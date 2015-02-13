package cz.larpovadatabaze.security;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.Pwd;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.HibernateException;

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
    private boolean setLanguage = true;

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

    public boolean isSetLanguage() {
        return setLanguage;
    }

    public void setSetLanguage(boolean setLanguage) {
        this.setLanguage = setLanguage;
    }

    /**
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean authenticate(final String username, final String password)
    {
        CsldUser authenticated =  null;
        try {
            authenticated = csldUserService.getByEmail(username);
        } catch(HibernateException ex) {
            authenticated = null;
        }
        if(authenticated == null) {
            return false;
        }
        if(!Pwd.validatePassword(password, authenticated.getPassword())) {
            authenticated = csldUserService.authenticate(username, Pwd.getMD5(password));
            if(authenticated != null){
                authenticated.setPassword(Pwd.generateStrongPasswordHash(password, username));
                csldUserService.saveOrUpdate(authenticated);
            }
        }

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
        if(!isSignedIn()){
            return null;
        }
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

    boolean clearRequested = false;
    public void requestClear(){
        clearRequested = true;
    }

    public void setClearRequested(boolean clearRequested){
        this.clearRequested = clearRequested;
    }

    public boolean isClearRequested(){
        return clearRequested;
    }


    public static CsldAuthenticatedWebSession get(){
        return (CsldAuthenticatedWebSession) Session.get();
    }
}