package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.Comments;
import cz.larpovadatabaze.services.Games;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *
 */
@Repository
@Transactional
public class SqlComments extends CRUD<Comment, Integer> implements Comments {
    private static final Logger logger = Logger.getLogger(SqlComments.class);

    private Games games;

    @Autowired
    public SqlComments(SessionFactory sessionFactory, Games games) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Comment.class)));
        this.games = games;
    }

    @Override
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        Criteria uniqueComment = crudRepository.getExecutableCriteria()
                .add(Restrictions.eq("user.id", userId))
                .add(Restrictions.eq("game.id", gameId));

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
        logger.info("Editor #"+ UserUtils.getLoggedUser().getId()+" hidden comment of user #"+comment.getUser().getId()+" for game #"+comment.getGame().getId());
    }

    @Override
    public void unHideComment(Comment comment) {
        if (Boolean.FALSE.equals(comment.getHidden())) return; // Nothing to do

        comment.setHidden(false);
        crudRepository.saveOrUpdate(comment);

        // Log
        logger.info("Editor #"+ UserUtils.getLoggedUser().getId()+" unhidden comment of user #"+comment.getUser().getId()+" for game #"+comment.getGame().getId());
    }

    @Override
    public Collection<Game> getGamesCommentedByUser(int userId) {
        return games.getGamesCommentedByUser(userId);
    }
}
