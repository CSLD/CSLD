package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;

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
}
