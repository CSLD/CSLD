package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Image;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
    public boolean addGame(Game game) {
        if (game.getCoverImage() != null) {
            if (game.getCoverImage().getPath() == null) game.setCoverImage(null);
        }
        if (game.getWeb() != null && !game.getWeb().isEmpty() && (!game.getWeb().startsWith("http://") &&
                !game.getWeb().startsWith("https://"))) {
            game.setWeb("http://" + game.getWeb());
        }

        return crudRepository.saveOrUpdate(game);
    }

    @Override
    public boolean saveOrUpdate(Game game) {
        game.setAdded(new Timestamp(new Date().getTime()));

        CsldUser logged = appUsers.getLoggedUser();
        game.setAddedBy(logged);

        if(game.getAmountOfComments() == null){
            game.setAmountOfComments(0);
        }
        if(game.getAmountOfRatings() == null){
            game.setAmountOfRatings(0);
        }
        if(game.getAmountOfPlayed() == null){
            game.setAmountOfPlayed(0);
        }
        if(game.getTotalRating() == null){
            game.setTotalRating(0d);
        }
        if(game.getAverageRating() == null){
            game.setAverageRating(0d);
        }
        if(game.getDescription() != null){
            game.setDescription(Jsoup.clean(game.getDescription(), Whitelist.basic()));
        }

        final List<FileUpload> uploads = (game.getCoverImage() != null)?game.getCoverImage().getFileUpload():null;
        if (uploads != null && uploads.size() > 0) {
            FileUpload upload = uploads.get(0);
            String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCoverImageStrategy()).path;
            try {
                Image image = new Image();
                image.setPath(filePath);
                game.setCoverImage(image);

                if(game.getVideo() == null ||
                        game.getVideo().getPath() == null ||
                        game.getVideo().getPath().equals("") ||
                        game.getVideo().getPath().equals("Video")){
                    //TODO problem when internationalizating.
                    game.setVideo(null);
                }

                return addGame(game);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to write file", e);
            }
        } else {
            if(game.getVideo() == null ||
                    game.getVideo().getPath() == null ||
                    game.getVideo().getPath().equals("") ||
                    game.getVideo().getPath().equals("Video")){
                game.setVideo(null);
            }

            return addGame(game);
        }
    }

    @Override
    public boolean canEditGame(Game game) {
        CsldUser loggedUser = appUsers.getLoggedUser();
        if(loggedUser != null){
            for(CsldUser author : game.getAuthors()) {
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
                .add(Restrictions.eq("ratings.user.id", userId));

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
