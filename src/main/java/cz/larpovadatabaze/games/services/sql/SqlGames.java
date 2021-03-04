package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.HtmlProcessor;
import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.entities.*;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.graphql.GraphQLUploadedFile;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Games stored in the SQL data store.
 */
@Repository
@Transactional
public class SqlGames extends CRUD<Game, Integer> implements Games {
    private SessionFactory sessionFactory;
    private FileService fileService;
    private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;
    private Images images;
    private AppUsers appUsers;

    @Autowired
    public SqlGames(SessionFactory sessionFactory, FileService fileService,
                    ImageResizingStrategyFactoryService imageResizingStrategyFactoryService,
                    Images images, AppUsers appUsers) {
        super(new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers)));

        this.sessionFactory = sessionFactory;
        this.fileService = fileService;
        this.imageResizingStrategyFactoryService = imageResizingStrategyFactoryService;
        this.images = images;
        this.appUsers = appUsers;
    }

    private ResourceReference iconResourceReference;

    @Override
    public void evictGame(Integer id) {
        Game g1 = crudRepository.findById(id);
        sessionFactory.getCurrentSession().evict(g1);
    }

    @Override
    public List<Game> getFirstChoices(String startsWith, int maxChoices) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .setMaxResults(maxChoices)
                .add(Restrictions.ilike("name", "%" + startsWith + "%"));

        return criteria.list();
    }

    @Override
    public List<Game> getLastGames(int limit) {
        DetachedCriteria subQueryCriteria = crudRepository.getBuilder().build();
        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = crudRepository.getExecutableCriteria();

        criteria.add(Subqueries.propertyIn("id", subQueryCriteria));
        criteria.setMaxResults(limit)
                .addOrder(Order.desc("added"));
        criteria.setFetchMode("availableLanguages", FetchMode.SELECT);

        return criteria.list();
    }

    @Override
    public List<Game> getMostPopularGames(int limit) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .setMaxResults(limit)
                .addOrder(Order.desc("totalRating"));

        return criteria.list();
    }

    @Override
    @Transactional
    public boolean saveOrUpdate(Game model) {
        return saveOrUpdate(model, null);
    }

    @Override
    @Transactional
    public boolean saveOrUpdate(Game model, GraphQLUploadedFile coverImageUpload) {
        model.setAdded(new Timestamp(new Date().getTime()));

        CsldUser logged = appUsers.getLoggedUser();
        model.setAddedBy(logged);

        String newDescription = model.getDescription();
        if (newDescription != null) {
            model.setDescription(
                    HtmlProcessor.sanitizeHtml(newDescription));
        }

        // Save video.
        if (!isValidVideoPresent(model.getVideo())) {
            model.setVideo(null);
        }

        if (model.getWeb() != null && !model.getWeb().isEmpty() && (!model.getWeb().startsWith("http://") &&
                !model.getWeb().startsWith("https://"))) {
            model.setWeb("http://" + model.getWeb());
        }

        final List<FileUpload> uploads = (model.getCoverImage() != null) ?
                model.getCoverImage().getFileUpload() : null;
        if (uploads != null && uploads.size() > 0) {
            FileUpload upload = uploads.get(0);
            String filePath = fileService.saveImageFileAndReturnPath(
                    new UploadedFile(upload), imageResizingStrategyFactoryService.getCoverImageStrategy()).path;
            model.setCoverImage(new Image(filePath));
        }

        if (coverImageUpload != null) {
            String filePath = fileService.saveImageFileAndReturnPath(
                    coverImageUpload, imageResizingStrategyFactoryService.getCoverImageStrategy()).path;
            model.setCoverImage(new Image(filePath));
        }

        if (model.getCoverImage() != null && model.getCoverImage().getPath() == null) {
            model.setCoverImage(null);
        }

        if (getById(model.getId()) != null) {
            model = (Game) sessionFactory.getCurrentSession().merge(model);
        }
        return crudRepository.saveOrUpdate(model);
    }

    private boolean isValidVideoPresent(Video video) {
        if (video == null) {
            return false;
        }
        String videoPath = video.getPath();
        return videoPath != null && !StringUtils.isEmpty(videoPath) &&
                !videoPath.equals("Video");
    }

    @Override
    public boolean canEditGame(Game game) {
        CsldUser loggedUser = appUsers.getLoggedUser();
        if (loggedUser != null) {
            for (CsldUser author : game.getAuthors()) {
                if (author.getId().equals(loggedUser.getId())) {
                    return true;
                }
            }
            if(loggedUser.getRole() >= CsldRoles.ADMIN.getRole()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Game> getGamesCommentedByUser(int userId) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .createAlias("game.comments", "comments")
                .add(Restrictions.eq("comments.user.id", userId));

        return new LinkedHashSet<Game>(criteria.list());
    }

    @Override
    public boolean isHidden(int gameId) {
        Game game = getById(gameId);
        return game != null && game.isDeleted();
    }

    @Override
    public void toggleGameState(int gameId) {
        Game game = crudRepository.findById(gameId);
        if(game == null) {
            return;
        }
        game.setDeleted(!game.isDeleted());
        if(game.isDeleted()) {
            for(Comment comment: game.getComments()){
                comment.setHidden(true);
            }
        } else {
            for(Comment comment: game.getComments()){
                comment.setHidden(false);
            }
        }

        crudRepository.saveOrUpdate(game);
    }

    @Override
    public Collection<Game> getGamesRatedByUser(int userId) {
        Criteria criteria = crudRepository.getExecutableCriteria()
                .createAlias("game.ratings", "ratings")
                .add(Restrictions.eq("ratings.user.id", userId))
                .add(Restrictions.isNotNull("ratings.rating"));

        return new LinkedHashSet<>(criteria.list());
    }

    @Override
    public ResourceReference getIconReference() {
        synchronized (this) {
            if (iconResourceReference == null) {
                iconResourceReference = images.createImageTypeResourceReference(crudRepository);
            }
        }

        return iconResourceReference;
    }
}
