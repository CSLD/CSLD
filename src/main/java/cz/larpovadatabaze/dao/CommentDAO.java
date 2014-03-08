package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:44
 */
@Repository
public class CommentDAO extends GenericHibernateDAO<Comment, Integer>{
    /**
     * It return comment of user on given game.
     *
     * @param userId id of user
     * @param gameId id of game
     * @return existing comment or null.
     */
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        Criteria uniqueComment = sessionFactory.getCurrentSession().createCriteria(Comment.class).
                add(Restrictions.eq("userId", userId)).
                add(Restrictions.eq("gameId", gameId));
        return (Comment) uniqueComment.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getLastComments(int maxComments) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Comment where is_hidden=false order by added desc");
        query.setMaxResults(maxComments);
        return query.list();
    }

    public int getAmountOfComments() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Comment.class);
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    public List<Comment> getLastComments(int first, int count) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Comment where is_hidden=false order by added desc");
        query.setFirstResult(first);
        query.setMaxResults(count);
        return query.list();
    }

    public List<Game> getGamesCommentedByUser(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select game from Game game join game.comments comments " +
                "where comments.userId = :id");
        query.setParameter("id", userId);
        return query.list();
    }
}
