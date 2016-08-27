package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.CommentBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Comment;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO: Do not return comments of hidden games.
 */
@Repository
public class CommentDAO extends GenericHibernateDAO<Comment, Integer>{
    @Override
    public IBuilder getBuilder() {
        return new CommentBuilder();
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
                .add(Restrictions.eq("user.id", userId))
                .add(Restrictions.eq("game.id", gameId));

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

    public List<Comment> getLastComments(int first, int count){
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("hidden", false))
                .addOrder(Order.desc("added"))
                .setMaxResults(count)
                .setFirstResult(first);

        return criteria.list();
    }
}
