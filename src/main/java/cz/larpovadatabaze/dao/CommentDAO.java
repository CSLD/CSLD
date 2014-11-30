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
    public List<Comment> getLastComments(int maxComments, Locale locale) {
        return getLastComments(0, maxComments, locale);
    }

    public List<Comment> getLastComments(int first, int count, Locale locale){
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("hidden", false))
                .createAlias("game", "commentedGame")
                .addOrder(Order.desc("added"))
                .setMaxResults(count)
                .setFirstResult(first);

        if(locale != null) {
            criteria.add(Restrictions.eq("commentedGame.lang", locale.getLanguage()));
        }

        return criteria.list();
    }

    public long getAmountOfComments(Locale locale) {
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .createAlias("game", "commentedGame")
                .setProjection(Projections.rowCount());
        criteria.add(Restrictions.eq("commentedGame.lang", locale.getLanguage()));

        return ((Long)criteria.uniqueResult()).intValue();
    }
}
