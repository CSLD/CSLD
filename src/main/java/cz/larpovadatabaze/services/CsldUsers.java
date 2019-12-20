package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;

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

    CsldUser getById(Integer id);

    List<CsldUser> getEditors();

    List<CsldUser> getAdmins();

    CsldUser authenticate(String username, String password);

    List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException;

    CsldUser getByEmail(String mail);

    boolean isLoggedAtLeastEditor();

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
