package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 21:46
 */
@Repository
public class CsldUserDAO extends GenericHibernateDAO<CsldUser, Integer> {
    @Qualifier("sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;
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
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select user from CsldUser user join user.commented commented " +
                "group by user.fbUser, user.id, user.imageId, user.password, user.personId, user.role    " +
                "order by count(commented) desc");
        return query.list();
    }

    public List<CsldUser> getOrderedByPlayed() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select user from CsldUser user join user.playedGames played " +
                "group by user.fbUser, user.id, user.imageId, user.password, user.personId, user.role " +
                "order by count(played) desc");
        return query.list();
    }
}
