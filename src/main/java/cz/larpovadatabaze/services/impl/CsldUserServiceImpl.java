package cz.larpovadatabaze.services.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import cz.larpovadatabaze.dao.CsldUserDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.utils.RandomString;

/**
 *
 */
@Repository(value = "csldUserService")
public class CsldUserServiceImpl implements CsldUserService {
    /**
     * Re-Captcha config - maybe move keys somewhere else?
     */
    private static final String RE_CAPTCHA_VERIFY_URL="https://www.google.com/recaptcha/api/siteverify";
    private static final String RE_CAPTCHA_SITE_KEY ="6LeEiv8SAAAAABn8qvmZGkez0Lpp-Pbak_Jr6T1t";
    private static final String RE_CAPTCHA_SECRET_KEY ="6LeEiv8SAAAAAAE2ikmbiEJhv5XdVaI4_TiPPEt6";

    @Autowired
    private CsldUserDAO csldUserDao;

    @Autowired
    private ImageService imageService;

    private ResourceReference userIconReference;

    @Override
    public CsldUser getById(Integer id) {
        return csldUserDao.findById(id);
    }

    @Override
    public boolean saveOrUpdate(CsldUser user) {
        return csldUserDao.saveOrUpdate(user);
    }

    @Override
    public void flush() {
        csldUserDao.flush();
    }

    @Override
    public List<CsldUser> getAll() {
        return csldUserDao.findAll();
    }

    @Override
    public List<CsldUser> getUnique(CsldUser example) {
        return csldUserDao.findByExample(example, new String[]{});
    }

    @Override
    public void remove(CsldUser toRemove) {
        csldUserDao.makeTransient(toRemove);
    }

    @Override
    public List<CsldUser> getEditors() {
        Criterion criterion = Restrictions.eq("role", new Short("2"));
        return csldUserDao.findByCriteria(criterion);
    }

    @Override
    public List<CsldUser> getAdmins() {
        Criterion criterion = Restrictions.eq("role", new Short("3"));
        return csldUserDao.findByCriteria(criterion);
    }

    @Override
    public CsldUser getWithMostComments() {
        return csldUserDao.getWithMostComments();
    }

    @Override
    public CsldUser getWithMostAuthored() {
        return csldUserDao.getWithMostAuthored();
    }

    @Override
    public CsldUser authenticate(String username, String password) {
        return csldUserDao.authenticate(username, password);
    }

    @Override
    public List<CsldUser> getAuthorsByBestGame(long first, long amountPerPage) {
        return csldUserDao.getAuthorsByBestGame(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        return csldUserDao.getByAutoCompletable(autoCompletable);
    }

    @Override
    public List<CsldUser> getOrderedUsersByName(long first, long amountPerPage) {
        return csldUserDao.getOrderedUsersByName(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getOrderedUsersByComments(long first, long amountPerPage) {
        return csldUserDao.gerOrderedUsersByComments(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getOrderedUsersByPlayed(long first, long amountPerPage) {
        return csldUserDao.getOrderedUsersByPlayed(first, amountPerPage);
    }

    @Override
    public CsldUser getByEmail(String mail) {
        return csldUserDao.getByEmail(mail);
    }

    @Override
    public int getAmountOfAuthors() {
        return csldUserDao.getAmountOfAuthors();
    }

    @Override
    public int getAmountOfOnlyAuthors() {
        return csldUserDao.getAmountOfOnlyAuthors();
    }

    @Override
    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        return csldUserDao.getFirstChoices(startsWith, maxChoices);
    }

    @Override
    public List<CsldUser> getAuthorsByName(long first, long amountPerPage) {
        return csldUserDao.getAuthorsByName(first, amountPerPage);
    }

    @Override
    public boolean isLoggedAtLeastEditor() {
        CsldUser user = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(user == null){
            return false;
        }
        return user.getRole() >= CsldRoles.getRoleByName("Editor");
    }

    @Override
    public boolean saveOrUpdateNewAuthor(CsldUser author) {
        author.setIsAuthor(true);
        if(author.getPerson().getEmail() == null){
            author.getPerson().setEmail(UUID.randomUUID().toString() + "@" + UUID.randomUUID().toString() + ".cz");
        }

        if(author.getPerson().getDescription() != null) {
            author.getPerson().setDescription(Jsoup.clean(author.getPerson().getDescription(), Whitelist.basic()));
        }
        author.setPassword(Pwd.generateStrongPasswordHash(new RandomString(12).nextString(), author.getPerson().getEmail()));
        return saveOrUpdate(author);
    }

    @Override
    public ResourceReference getIconReference() {
        // Reference is singleton, lazy-inited
        synchronized(this) {
            if (userIconReference == null) {
                userIconReference = imageService.createImageTypeResourceReference(csldUserDao);
            }
        }
        return userIconReference;
    }

    @Override
    public String getReCaptchaSiteKey() {
        return RE_CAPTCHA_SITE_KEY;
    }

    @Override
    public boolean checkReCaptcha(String response, String remoteIp) throws ReCaptchaTechnicalException {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(RE_CAPTCHA_VERIFY_URL);
        post.addParameter("secret", RE_CAPTCHA_SECRET_KEY);
        post.addParameter("response", response);
        post.addParameter("remoteip", remoteIp);

        try {
            int code = client.executeMethod(post);
            if (code != HttpStatus.SC_OK) {
                throw new Exception("Could not send request: "+post.getStatusLine());
            }

            String responseFromGoogle = post.getResponseBodyAsString();
            JSONObject responseObject = new JSONObject(responseFromGoogle);
            return responseObject.getBoolean("success");

        }
        catch(Exception e) {
            throw new ReCaptchaTechnicalException(e);
        }
    }
}
