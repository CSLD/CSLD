package cz.larpovadatabaze.services;

import java.util.List;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;

/**
 *
 */
public interface CsldUserService extends GenericService<CsldUser>, IIconReferenceProvider<CsldUser> {
    public static final int USER_IMAGE_SIZE=120;
    public static final int USER_IMAGE_LEFTTOP_PERCENT=10;

    public static class ReCaptchaTechnicalException extends Exception {
        public ReCaptchaTechnicalException(Throwable throwable) {
            super(throwable);
        }
    }

    public CsldUser getById(Integer id);

    boolean saveOrUpdate(CsldUser user);

    void flush();

    List<CsldUser> getEditors();

    List<CsldUser> getAdmins();

    CsldUser getWithMostComments();

    CsldUser getWithMostAuthored();

    CsldUser authenticate(String username, String password);

    List<CsldUser> getAuthorsByBestGame(long first, long amountPerPage);

    List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException;

    List<CsldUser> getOrderedUsersByName(long first, long amountPerPage);

    List<CsldUser> getOrderedUsersByComments(long first, long amountPerPage);

    List<CsldUser> getOrderedUsersByPlayed(long first, long amountPerPage);

    CsldUser getByEmail(String mail);

    int getAmountOfAuthors();

    int getAmountOfOnlyAuthors();

    List<CsldUser> getAuthorsByName(long first, long amountPerPage);

    boolean isLoggedAtLeastEditor();

    /**
     * It updates given author, with all data that user is not forced to add, but we actually need
     * for the database for author like generated email.
     *
     * @param author
     * @return
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
