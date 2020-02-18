package cz.larpovadatabaze.users.services.sql;

import com.github.openjson.JSONObject;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.entities.Image;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.users.Pwd;
import cz.larpovadatabaze.users.RandomString;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.services.EmailAuthentications;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
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
    private final static Logger logger = Logger.getLogger(SqlCsldUsers.class);
    /**
     * Re-Captcha config - maybe move keys somewhere else?
     */
    private static final String RE_CAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String RE_CAPTCHA_SITE_KEY = "6LeEiv8SAAAAABn8qvmZGkez0Lpp-Pbak_Jr6T1t";
    private static final String RE_CAPTCHA_SECRET_KEY = "6LeEiv8SAAAAAAE2ikmbiEJhv5XdVaI4_TiPPEt6";

    private Images images;
    private MailService mails;
    private EmailAuthentications authentications;
    private FileService files;
    private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;
    private Ratings ratings;
    private Comments comments;
    private Upvotes upvotes;
    private Events events;
    private Labels labels;
    private Photos photos;
    private AppUsers appUsers;
    private Games games;
    private SessionFactory sessionFactory;

    @Autowired
    public SqlCsldUsers(SessionFactory sessionFactory, Images images,
                        MailService mails, EmailAuthentications authentications, FileService files,
                        ImageResizingStrategyFactoryService imageResizingStrategyFactoryService,
                        Ratings ratings, Comments comments, Upvotes upvotes, Events events, Labels labels,
                        Photos photos, AppUsers appUsers, Games games) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(CsldUser.class)));
        this.sessionFactory = sessionFactory;
        this.images = images;
        this.mails = mails;
        this.authentications = authentications;
        this.files = files;
        this.imageResizingStrategyFactoryService = imageResizingStrategyFactoryService;
        this.ratings = ratings;
        this.comments = comments;
        this.upvotes = upvotes;
        this.events = events;
        this.labels = labels;
        this.photos = photos;
        this.appUsers = appUsers;
        this.games = games;
    }

    private ResourceReference userIconReference;

    @Override
    public void sendForgottenPassword(CsldUser user, EmailAuthentication emailAuthentication, String url) {
        String content = String.format("Pro vytvoření nového hesla použijte následující odkaz: %s", url);
        String subject = "[LarpDB] Zaslani odkazu na vytvoreni noveho hesla.";

        mails.sendForgottenPassword(user, subject, content);

        authentications.saveOrUpdate(emailAuthentication);
    }

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
        return crudRepository.findSingleByCriteria(
                Restrictions.and(
                        Restrictions.eq("person.email", username),
                        Restrictions.eq("password", password))
        );
    }

    @Override
    public CsldUser getByEmail(String mail) {
        return crudRepository.findSingleByCriteria(
                Restrictions.eq("person.email", mail).ignoreCase()
        );
    }

    @Override
    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .setMaxResults(maxChoices)
                .add(Restrictions.or(
                        Restrictions.ilike("person.name", "%" + startsWith + "%"),
                        Restrictions.ilike("person.nickname", "%" + startsWith + "%")));

        return criteria.list();
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
                userIconReference = images.createImageTypeResourceReference(crudRepository);
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

        } catch (Exception e) {
            throw new ReCaptchaTechnicalException(e);
        }
    }

    @Override
    public boolean saveOrUpdate(CsldUser model, List<FileUpload> uploads) {
        CsldUser currentInSession = getById(model.getId());
        String description = model.getPerson().getDescription();
        if (description != null) {
            currentInSession.getPerson().setDescription(
                    Jsoup.clean(description, Whitelist.basic()));
        }
        currentInSession.setPerson(model.getPerson());
        if (model.getPassword() != null) {
            currentInSession.setPassword(
                    Pwd.generateStrongPasswordHash(model.getPassword(), model.getPerson().getEmail())
            );
        }
        if (uploads != null && uploads.size() > 0) {
            for (FileUpload upload : uploads) {
                String filePath = files.saveImageFileAndReturnPath(new UploadedFile(upload),
                        imageResizingStrategyFactoryService.getCuttingSquareStrategy(
                                CsldUsers.USER_IMAGE_SIZE, CsldUsers.USER_IMAGE_LEFTTOP_PERCENT)).path;
                try {
                    Image image = new Image();
                    image.setPath(filePath);
                    currentInSession.setImage(image);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        }
        return crudRepository.saveOrUpdate(currentInSession);
    }

    @Override
    public CsldUser byFbId(String fbId) {
        return crudRepository.findSingleByCriteria(
                Restrictions.eq("fbId", fbId)
        );
    }

    @Override
    public void remove(CsldUser toRemoveDto) {
        if (!appUsers.isAtLeastEditor()) {
            logger.warn("Try to remove user: " + toRemoveDto.getId() + " By: " + appUsers.getLoggedUserId());
            return;
        }

        CsldUser toRemove = getById(toRemoveDto.getId());
        // Remove from authors but keep the games
        toRemove.getAuthorOf()
                .forEach(game -> {
                    game.getAuthors().remove(toRemove);
                    games.saveOrUpdate(game);

                    sessionFactory.getCurrentSession()
                            .createSQLQuery("delete from csld_game_has_author " +
                                    "where id_user = :idUser and id_game = :idGame")
                            .setParameter("idUser", toRemove.getId())
                            .setParameter("idGame", game.getId())
                            .executeUpdate();
                });

        // Remove comments
        comments.removeForUser(toRemove);
        // Remove ratings
        ratings.removeForUser(toRemove);
        // Remove upvotes
        upvotes.removeForUser(toRemove);
        // Replace as added by for Events
        events.removeAddedBy(toRemove);
        // Replace as added by for Labels
        labels.removeAddedBy(toRemove);
        // Remove as author from photos
        photos.removeAddedBy(toRemove);

        super.remove(toRemove);
    }
}
