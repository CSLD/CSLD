package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 21:46
 */
@Repository
public class CsldUserDAO extends GenericHibernateDAO<CsldUser, Integer> {
    @Autowired
    private GameDAO gameDao;

    public List<CsldUser> getAuthorsByGames() {
        Session session = sessionFactory.getCurrentSession();
        List<CsldUser> orderedAuthors = null;
        findByCriteria();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.createQuery("select user from CsldUser user join user.authorOf authored " +
                    "group by user.fbUser, user.id, user.imageId, user.password, user.personId, user.role    " +
                    "having count(authored) > 0 " +
                    "order by count(authored)");
            orderedAuthors = query.list();
            tx.commit();
        } catch (RuntimeException ex) {
            tx.rollback();
            ex.printStackTrace();
            throw ex;
        }
        return orderedAuthors;
    }

    public List<CsldUser> getAuthorsByBestGame() {
        List<CsldUser> authors = new ArrayList<CsldUser>();
        List<Game> ratedGames = gameDao.getRated();
        for(Game rated : ratedGames) {
            authors.addAll(rated.getAuthors());
        }
        return authors;
    }

    public CsldUser getWithMostComments() {
        Session session = sessionFactory.getCurrentSession();
        CsldUser author = null;
        findByCriteria();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.createQuery("select user from CsldUser user join user.commented commented " +
                    "group by user.fbUser, user.id, user.imageId, user.password, user.personId, user.role    " +
                    "having count(commented) > 0 " +
                    "order by count(commented) desc");
            List<CsldUser> users = query.list();
            author = (users.size() > 0) ? users.get(0): null;
            tx.commit();
        } catch (RuntimeException ex) {
            tx.rollback();
            ex.printStackTrace();
            throw ex;
        }
        return author;
    }

    public CsldUser getWithMostAuthored() {
        Session session = sessionFactory.getCurrentSession();
        List<CsldUser> orderedAuthors = null;
        findByCriteria();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.createQuery("select user from CsldUser user join user.authorOf authored " +
                    "group by user.fbUser, user.id, user.imageId, user.password, user.personId, user.role    " +
                    "having count(authored) > 0 " +
                    "order by count(authored) desc");
            orderedAuthors = query.list();
            tx.commit();
        } catch (RuntimeException ex) {
            tx.rollback();
            ex.printStackTrace();
            throw ex;
        }
        return (orderedAuthors.size() > 0 ) ? orderedAuthors.get(0) : null;
    }

    public CsldUser authenticate(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        List<CsldUser> orderedAuthors = null;
        findByCriteria();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.createQuery("select user from CsldUser user join user.person person " +
                    " where person.email = :username and user.password = :password");
            query.setString("username", username);
            query.setString("password", password);
            orderedAuthors = query.list();
            tx.commit();
        } catch (RuntimeException ex) {
            tx.rollback();
            ex.printStackTrace();
            throw ex;
        }
        if(orderedAuthors.size() > 0){
            return orderedAuthors.get(0);
        } else {
            return null;
        }
    }

    public List<CsldUser> getOrderedByName() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select user from CsldUser user join user.person person" +
                " order by person.name");
        return query.list();
    }

    public List<CsldUser> getOrderedByComments() {
        List<CsldUser> allUsers = findAll();
        Collections.sort(allUsers, new Comparator<CsldUser>() {
            @Override
            public int compare(CsldUser o1, CsldUser o2) {
                return o2.getCommented().size() - o1.getCommented().size();
            }
        });
        return allUsers;
    }

    public List<CsldUser> getOrderedByPlayed() {
        List<CsldUser> allUsers = findAll();
        Collections.sort(allUsers, new Comparator<CsldUser>() {
            @Override
            public int compare(CsldUser o1, CsldUser o2) {
                return o2.getPlayedGames().size() - o1.getPlayedGames().size();
            }
        });
        return allUsers;
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param autoCompletable Expected format is {Name, Email} Email is unique identifier of person as well as user.
     * @return It should return only single user or no user if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        String[] personData = autoCompletable.split(", ");
        if(personData.length < 2) {
            throw new WrongParameterException();
        }
        String email = personData[1];
        Criteria uniqueUser = sessionFactory.getCurrentSession().createCriteria(CsldUser.class).
                createCriteria("person").add(
                Restrictions.eq("form.email", email)
        );
        return uniqueUser.list();
    }
}
