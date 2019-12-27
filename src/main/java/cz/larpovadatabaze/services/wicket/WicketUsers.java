package cz.larpovadatabaze.services.wicket;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.AppUsers;
import cz.larpovadatabaze.services.CsldUsers;
import org.springframework.stereotype.Service;

/**
 * User: Michal Kara
 * Date: 4.1.14
 * Time: 11:45
 */
@Service
public class WicketUsers implements AppUsers {
    public WicketUsers() {
    }

    @Override
    public boolean isSignedIn() {
        return CsldAuthenticatedWebSession.get().isSignedIn();
    }

    @Override
    public CsldUser getLoggedUser() {
        if (!isSignedIn()) {
            return null;
        }

        System.out.println("Loading information about user.");
        int actualUserId = CsldAuthenticatedWebSession.get().getLoggedUser().getId();
        CsldUsers userService = (CsldUsers) Csld.getApplicationContext().getBean("csldUsers");
        return userService.getById(actualUserId);
    }

    @Override
    public Integer getLoggedUserId() {
        return getLoggedUser().getId();
    }

    @Override
    public boolean isEditor() {
        CsldUser user = CsldAuthenticatedWebSession.get().getLoggedUser();
        if (user == null) return false;

        return user.getRole().shortValue() >= CsldRoles.EDITOR.getRole().shortValue();
    }

    @Override
    public boolean isAdmin() {
        CsldUser user = CsldAuthenticatedWebSession.get().getLoggedUser();
        if (user == null) return false;

        return user.getRole().equals(CsldRoles.EDITOR.getRole());
    }

    @Override
    public boolean isAtLeastEditor() {
        return isEditor() || isAdmin();
    }

    @Override
    public boolean isAdminOfGroup() {
        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if (isVisible) {
            CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();
            if (logged == null) {
                isVisible = false;
            }
            if (logged != null && logged.getRole() <= CsldRoles.USER.getRole()) {
                isVisible = false;
            }
        }

        return isVisible;
    }
}
