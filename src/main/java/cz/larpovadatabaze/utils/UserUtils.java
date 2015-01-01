package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;

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
        return CsldAuthenticatedWebSession.get().isSignedIn();
    }

    /**
     * @return Logged-in user
     */
    public static CsldUser getLoggedUser() {
        if(!isSignedIn()) {
            return null;
        }

        int actualUserId = CsldAuthenticatedWebSession.get().getLoggedUser().getId();
        CsldUserService userService = (CsldUserService) Csld.getApplicationContext().getBean("csldUserService");
        return userService.getById(actualUserId);
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

    /**
     * User is Editor or Admin.
     *
     * @return True if user is editor or admin.
     */
    public static boolean isAtLeastEditor() {
        return isEditor() || isAdmin();
    }

    public static boolean isAdminOfGroup(CsldGroup group) {
        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged == null){
                isVisible = false;
            }
            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                if(!group.getAdministrators().contains(logged)){
                    isVisible = false;
                }
            }
        }

        return isVisible;
    }
}
