package cz.larpovadatabaze.users.services.sql;

import com.github.openjson.JSONObject;
import cz.larpovadatabaze.common.dao.CsldUserDAO;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.exceptions.WrongParameterException;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.RandomString;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * CsldUsers stored in the SQL Data Store.
 */
@Repository(value = "csldUsers")
@Transactional
public class SqlCsldUsers extends CRUD<CsldUser, Integer> implements CsldUsers {
    /**
     * Re-Captcha config - maybe move keys somewhere else?
     */
    private static final String RE_CAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String RE_CAPTCHA_SITE_KEY = "6LeEiv8SAAAAABn8qvmZGkez0Lpp-Pbak_Jr6T1t";
    private static final String RE_CAPTCHA_SECRET_KEY = "6LeEiv8SAAAAAAE2ikmbiEJhv5XdVaI4_TiPPEt6";

    private CsldUserDAO csldUserDao;
    private Images images;

    @Autowired
    public SqlCsldUsers(CsldUserDAO csldUserDao, Images images) {
        super(csldUserDao);
        this.csldUserDao = csldUserDao;
        this.images = images;
    }

    private ResourceReference userIconReference;

    @Override
    public List<CsldUser> getEditors() {
        Criterion criterion = Restrictions.eq("role", Short.valueOf("2"));
        return crudRepository.findByCriteria(criterion);
    }

    @Override
    public List<CsldUser> getAdmins() {
        Criterion criterion = Restrictions.eq("role", Short.valueOf("3"));
        return crudRepository.findByCriteria(criterion);
    }

    @Override
    public CsldUser authenticate(String username, String password) {
        return csldUserDao.authenticate(username, password);
    }

    @Override
    public List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        return csldUserDao.getByAutoCompletable(autoCompletable);
    }

    @Override
    public CsldUser getByEmail(String mail) {
        return csldUserDao.getByEmail(mail);
    }

    @Override
    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        return csldUserDao.getFirstChoices(startsWith, maxChoices);
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
                userIconReference = images.createImageTypeResourceReference(csldUserDao);
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
