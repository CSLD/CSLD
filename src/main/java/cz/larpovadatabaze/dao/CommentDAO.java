package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class CommentDAO extends GenericHibernateDAO<Comment, Integer>{
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Comment>(Comment.class);
    }

    /**
     * It return comment of user on given game.
     *
     * @param userId id of user
     * @param gameId id of game
     * @return existing comment or null.
     */
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        Criteria uniqueComment = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("gameId", gameId));

        return (Comment) uniqueComment.uniqueResult();
    }

    public int getAmountOfComments() {
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getLastComments(int maxComments) {
        return getLastComments(0, maxComments);
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getLastComments(int first, int count) {
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("hidden", false))
                .addOrder(Order.desc("added"))
                .setMaxResults(count)
                .setFirstResult(first);

        return criteria.list();
    }
}
