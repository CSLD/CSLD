package cz.larpovadatabaze.users.services;

import cz.larpovadatabaze.common.entities.CsldUser;

public interface AppUsers {
    /**
     * @return Whether user is signed in
     */
    boolean isSignedIn();

    /**
     * @return Logged-in user
     */
    CsldUser getLoggedUser();

    /**
     * Return Id of the logged in user or null if there is noone logged in the system.
     *
     * @return Id ot null.
     */
    Integer getLoggedUserId();

    /**
     * @return Whether logged in user is editor
     */
    boolean isEditor();

    /**
     * @return Whether logged in user is global admin
     */
    boolean isAdmin();

    /**
     * User is Editor or Admin.
     *
     * @return True if user is editor or admin.
     */
    boolean isAtLeastEditor();

    /**
     * User has sufficient rights to administer groups.
     *
     * @return True if the user is admin of the group.
     */
    boolean isAdminOfGroup();

    /**
     * Log in
     *
     * @param email Email to log in with
     * @param password Password to log in with
     *
     * @return Whether login was successful
     */
    boolean signIn(String email, String password);

    /**
     * Log out
     */
    void signOut();
}
