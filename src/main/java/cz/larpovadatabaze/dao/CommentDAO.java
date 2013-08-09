package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Comment;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
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
     * @param userId
     * @param gameId
     * @return existing comment or null.
     */
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        Criteria uniqueComment = sessionFactory.getCurrentSession().createCriteria(Comment.class).
                add(Restrictions.eq("userId", userId)).
                add(Restrictions.eq("gameId", gameId));
        return (Comment) uniqueComment.uniqueResult();
    }

    /**
     * It return comments ordered from the last added.
     *
     * @return
     */
    public List<Comment> getLastComments() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Comment order by added desc");
        return query.list();
    }
}
