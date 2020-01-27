package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Upvote;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Upvotes;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class SqlUpvotes extends CRUD<Upvote, Integer> implements Upvotes {
    @Autowired
    public SqlUpvotes(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Upvote.class)));
    }

    @Override
    public void upvote(CsldUser user, Comment comment) {
        Upvote toAdd = new Upvote();
        toAdd.setUser(user);
        toAdd.setComment(comment);
        toAdd.setAdded(new Timestamp(new Date().getTime()));

        saveOrUpdate(toAdd);
    }

    @Override
    public void downvote(CsldUser user, Comment comment) {
        Upvote toRemove = new Upvote();
        toRemove.setComment(comment);
        toRemove.setUser(user);

        List<Upvote> upvotes = crudRepository.findByExample(toRemove);
        for (Upvote upvote : upvotes) {
            crudRepository.delete(upvote);
        }
    }

    @Override
    public Collection<Upvote> forUserAndComment(CsldUser user, Comment comment) {
        return crudRepository.findByCriteria(Restrictions.and(
                Restrictions.eq("comment", comment),
                Restrictions.eq("user", user)
        ));
    }
}
