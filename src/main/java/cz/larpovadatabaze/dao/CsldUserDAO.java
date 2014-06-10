package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
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
public class CsldUserDAO extends GenericHibernateDAO<CsldUser, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<CsldUser>(CsldUser.class);
    }

    public int getAmountOfAuthors() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CsldUser.class)
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByBestGame(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.desc("bestGame.totalRating"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    public CsldUser getWithMostComments() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.desc("amountOfComments"))
                .setMaxResults(1);

        return (CsldUser) criteria.uniqueResult();
    }

    public CsldUser getWithMostAuthored() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.desc("amountOfCreated"))
                .setMaxResults(1);

        return (CsldUser) criteria.uniqueResult();
    }

    public CsldUser authenticate(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.eq("person.email",username))
                .add(Restrictions.eq("password",password));

        return (CsldUser) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getOrderedUsersByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .createAlias("csldUser.person", "person")
                .addOrder(Order.asc("person.name"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> gerOrderedUsersByComments(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.desc("amountOfComments"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getOrderedUsersByPlayed(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.desc("amountOfComments"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
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
        Criteria uniqueUser = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("person.email", email));

        return uniqueUser.list();
    }

    public CsldUser getByEmail(String mail) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.eq("person.email",mail).ignoreCase());

        return (CsldUser) criteria.uniqueResult();
    }

    public int getAmountOfOnlyAuthors() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.isNotEmpty("authorOf"))
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getAuthorsByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.isNotEmpty("authorOf"))
                .addOrder(Order.asc("person.name"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .setMaxResults(maxChoices)
                .add(Restrictions.or(
                    Restrictions.ilike("person.name", "%" + startsWith + "%"),
                    Restrictions.ilike("person.nickname", "%" + startsWith + "%")));

        return criteria.list();
    }
}
