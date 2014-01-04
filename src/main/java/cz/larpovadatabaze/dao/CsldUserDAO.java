package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
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
 * Date: 27.3.13
 * Time: 21:46
 */
@Repository
public class CsldUserDAO extends GenericHibernateDAO<CsldUser, Integer> {
    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByGames(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser where amountOfCreated > 0 order by amountOfCreated desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByGames() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser where amountOfCreated > 0 order by amountOfCreated desc");
        return query.list();
    }

    public int getAmountOfAuthors() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CsldUser.class);
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByBestGame(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser order by bestGame.totalRating desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    public CsldUser getWithMostComments() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("" +
                "from CsldUser user order by amountOfComments desc");
        query.setMaxResults(1);
        return (CsldUser) query.uniqueResult();
    }

    public CsldUser getWithMostAuthored() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("" +
                "from CsldUser user order by amountOfCreated desc");
        query.setMaxResults(1);
        return (CsldUser) query.uniqueResult();
    }

    public CsldUser authenticate(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from CsldUser user " +
                " where user.person.email = :username and user.password = :password");
        query.setString("username", username);
        query.setString("password", password);
        return (CsldUser) query.uniqueResult();
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
        Criteria uniqueUser = sessionFactory.getCurrentSession().createCriteria(CsldUser.class)
                .add(Restrictions.eq("person.email", email));
        return uniqueUser.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getOrderedUsersByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("" +
                "from CsldUser user order by user.person.name");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> gerOrderedUsersByComments(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser user order by amountOfComments desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getOrderedUsersByPlayed(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser user order by amountOfPlayed desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    public CsldUser getByEmail(String mail) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser user where lower(user.person.email) = :mail");
        query.setParameter("mail", mail.toLowerCase());
        return (CsldUser) query.uniqueResult();
    }

    public int getAmountOfOnlyAuthors() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CsldUser.class);
        criteria.add(Restrictions.isNotEmpty("authorOf"));
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldUser where amountOfCreated > 0 order by person.name");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        Session session = sessionFactory.getCurrentSession();
        Criteria query = session.createCriteria(CsldUser.class);
        query.setMaxResults(maxChoices);
        query.add(Restrictions.or(
                Restrictions.ilike("person.name","%"+startsWith+"%"),
                Restrictions.ilike("person.nickname","%"+startsWith+"%")));

        return query.list();
    }
}
