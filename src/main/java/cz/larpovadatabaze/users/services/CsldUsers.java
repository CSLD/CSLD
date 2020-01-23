package cz.larpovadatabaze.users.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.services.CRUDService;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;

import java.util.List;

/**
 *
 */
public interface CsldUsers extends CRUDService<CsldUser, Integer>, IIconReferenceProvider<CsldUser> {
    int USER_IMAGE_SIZE = 120;
    int USER_IMAGE_LEFTTOP_PERCENT = 10;

    class ReCaptchaTechnicalException extends Exception {
        public ReCaptchaTechnicalException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * Return all the editors in the store. The administrator isn't an editor.
     *
     * @return List of all the editors.
     */
    List<CsldUser> getEditors();

    /**
     * Return all administrator in the store.
     *
     * @return List of all the administrators.
     */
    List<CsldUser> getAdmins();

    /**
     * Verify whether the User with given Username and Password is a valid user.
     *
     * @param username Email of the user
     * @param password Password of the user already as the hash.
     * @return Valid user or null if there is no user with given credentials.
     */
    CsldUser authenticate(String username, String password);

    /**
     * Return the user with given email. If there is none with the mail return null.
     *
     * @param mail Email to seek after.
     * @return Valid user or null.
     */
    CsldUser getByEmail(String mail);

    /**
     * It updates given author, with all data that user is not forced to add, but we actually need
     * for the database for author like generated email.
     *
     * @param author
     * @return True if the author was properly created
     */
    boolean saveOrUpdateNewAuthor(CsldUser author);

    /**
\     * @return Site key for re-captcha
     */
    String getReCaptchaSiteKey();

    /**
     * Checks re-captcha response
     *
     * @param response Re-captcha response from post
     * @param remoteIp User ip
     *
     * @return Whether re-captcha is valid
     *
     * @throws ReCaptchaTechnicalException When there are technical problems connecting to re-captcha
     */
    boolean checkReCaptcha(String response, String remoteIp) throws ReCaptchaTechnicalException;
}
