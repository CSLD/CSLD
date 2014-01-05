package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;

/**
 * User: Michal Kara
 * Date: 4.1.14
 * Time: 11:45
 */
public class UserUtils {
    /**
     * @return Whether user is signed in
     */
    public static boolean isSignedIn() {
        return ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).isSignedIn();
    }

    /**
     * @return Logged-in user
     */
    public static CsldUser getLoggedUser() {
        return ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
    }

    /**
     * @return Whether logged in user is editor
     */
    public static boolean isEditor() {
        CsldUser user = getLoggedUser();
        if (user == null) return false;

        return user.getRole().shortValue() >= CsldRoles.EDITOR.getRole().shortValue();
    }

    /**
     * @return Whether logged in user is global admin
     */
    public static boolean isAdmin() {
        CsldUser user = getLoggedUser();
        if (user == null) return false;

        return user.getRole().equals(CsldRoles.EDITOR.getRole());
    }
}
