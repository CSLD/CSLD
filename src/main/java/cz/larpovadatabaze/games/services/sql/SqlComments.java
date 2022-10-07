package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.HtmlProcessor;
import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.models.Page;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class SqlComments extends CRUD<Comment, Integer> implements Comments {
    private static final Logger logger = LogManager.getLogger();;
    private final String GAME_BY_ID = "game.id";
    private final String USER_BY_ID = "user.id";

    private Games games;
    private AppUsers appUsers;

    @Autowired
    public SqlComments(SessionFactory sessionFactory, Games games, AppUsers appUsers) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Comment.class)));
        this.games = games;
        this.appUsers = appUsers;
    }

    @Override
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        Criteria uniqueComment = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq(USER_BY_ID, userId))
                .add(Restrictions.eq(GAME_BY_ID, gameId));

        Comment result = (Comment) uniqueComment.uniqueResult();
        return result;
    }

    @Override
    public int getAmountOfComments() {
        Criteria amountOfComments = crudRepository.getExecutableCriteria()
                .setProjection(Projections.rowCount());

        return ((Long) amountOfComments.uniqueResult()).intValue();
    }

    @Override
    public boolean saveOrUpdate(Comment actualComment) {
        actualComment.setComment(HtmlProcessor.sanitizeHtml(actualComment.getComment()));
        boolean result = crudRepository.saveOrUpdate(actualComment);

        games.evictGame(actualComment.getGame().getId());
        return result;
    }

    @Override
    public Collection<Comment> getLastComments(int count) {
        return new LinkedHashSet<>(getLastComments(0, count));
    }

    @Override
    public Collection<Comment> getLastComments(int first, int count) {
        Criteria lastComments = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("hidden", false))
                .addOrder(Order.desc("added"))
                .setMaxResults(count)
                .setFirstResult(first);

        return new LinkedHashSet<Comment>(lastComments.list());
    }

    @Override
    public void hideComment(Comment comment) {
        if (Boolean.TRUE.equals(comment.getHidden())) return; // Nothing to do

        comment.setHidden(true);
        crudRepository.saveOrUpdate(comment);

        // Log
        logger.info("Editor #" + appUsers.getLoggedUserId() + " hidden comment of user #" + comment.getUser().getId() + " for game #" + comment.getGame().getId());
    }

    @Override
    public void unHideComment(Comment comment) {
        if (Boolean.FALSE.equals(comment.getHidden())) return; // Nothing to do

        comment.setHidden(false);
        crudRepository.saveOrUpdate(comment);

        // Log
        logger.info("Editor #" + appUsers.getLoggedUserId() + " unhidden comment of user #" + comment.getUser().getId() + " for game #" + comment.getGame().getId());
    }

    @Override
    public List<Comment> visibleForCurrentUserOrderedByRecent(CsldUser user, Page page) {
        Criterion restrictions = visibleForCurrentUserAndObject(user.getId(), USER_BY_ID);
        Criteria comments = crudRepository.getExecutableCriteria()
                .add(restrictions)
                .setFirstResult(page.from)
                .setMaxResults(page.size)
                .addOrder(Order.desc("added"));

        return comments.list();
    }

    @Override
    public int amountOfCommentsVisibleForCurrentUserAndUser(CsldUser user) {
        Criterion restrictions = visibleForCurrentUserAndObject(user.getId(), USER_BY_ID);
        Criteria comments = crudRepository.getExecutableCriteria()
                .add(restrictions)
                .setProjection(Projections.rowCount());
        return ((Long) comments.uniqueResult()).intValue();
    }

    @Override
    public List<Comment> visibleForCurrentUserOrderedByUpvotes(Game game, Page page) {
        Criterion restrictions = visibleForCurrentUserAndObject(game.getId(), GAME_BY_ID);
        Criteria comments = crudRepository.getExecutableCriteria()
                .add(restrictions)
                .setFirstResult(page.from)
                .setMaxResults(page.size)
                .addOrder(Order.desc("amountOfUpvotes"))
                .addOrder(Order.desc("added"));
        return comments.list();
    }

    @Override
    public int amountOfCommentsVisibleForCurrentUserAndGame(Game game) {
        Criterion restrictions = visibleForCurrentUserAndObject(game.getId(), GAME_BY_ID);
        Criteria unsorted = crudRepository.getExecutableCriteria()
                .add(restrictions).setProjection(Projections.rowCount());
        return ((Long) unsorted.uniqueResult()).intValue();
    }

    private Criterion visibleForCurrentUserAndObject(Integer id, String objectById) {
        Criterion restrictions;
        if (appUsers.isAtLeastEditor()) {
            restrictions = Restrictions.eq(objectById, id);
        } else if (appUsers.isSignedIn()) {
            restrictions = Restrictions.and(
                    Restrictions.eq(objectById, id),
                    Restrictions.or(
                            Restrictions.eq("hidden", false),
                            Restrictions.eq(USER_BY_ID, appUsers.getLoggedUserId())
                    )
            );
        } else {
            restrictions = Restrictions.and(
                    Restrictions.eq(objectById, id),
                    Restrictions.eq("hidden", false)
            );
        }

        return restrictions;
    }

    @Override
    public void removeForUser(CsldUser toRemove) {
        List<Comment> comments = crudRepository.findByCriteria(
                Restrictions.eq("user", toRemove));

        comments.forEach(this::remove);
    }
}
