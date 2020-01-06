package cz.larpovadatabaze.common.dao;

import cz.larpovadatabaze.common.api.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Upvote;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Upvote and downvote entities. Initially developed for the Comments.
 */
@Repository
public class UpvoteDAO extends GenericHibernateDAO<Upvote, Integer> {
    public UpvoteDAO(SessionFactory sessionFactory) {
        super(sessionFactory, new GenericBuilder<>(Upvote.class));
    }

    /**
     * Create Upvote based on the parameters and insert it into the database.
     *
     * @param user    User upvoting
     * @param comment Comment to be upvoted.
     */
    public void upvote(CsldUser user, Comment comment) {
        Upvote toAdd = new Upvote();
        toAdd.setUser(user);
        toAdd.setComment(comment);
        toAdd.setAdded(new Timestamp(new Date().getTime()));

        saveOrUpdate(toAdd);
    }

    /**
     * Remove all Upvotes based on the parameters from the database. There should always be only one
     * but if multiple arises, this removes all.
     * @param user User downvoting
     * @param comment Comment to be downvoted
     */
    public void downvote(CsldUser user, Comment comment) {
        Upvote toRemove = new Upvote();
        toRemove.setComment(comment);
        toRemove.setUser(user);

        List<Upvote> upvotes = findByExample(toRemove);
        for(Upvote upvote: upvotes) {
            delete(upvote);
        }
    }
}
