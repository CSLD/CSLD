package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class GroupDAO extends GenericHibernateDAO<CsldGroup, Integer> {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<CsldGroup>(CsldGroup.class);
    }

    @SuppressWarnings("unchecked")
    public List<CsldGroup> orderedByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .addOrder(Order.asc("name"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param groupName Expected format is {Name} Name is unique identifier of group.
     * @return It should return only single group or no group if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException {
        Criteria uniqueGroup = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("name", groupName));

        return uniqueGroup.list();
    }

    public int getAmountOfGroups() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    public int getAverageOfGroup(CsldGroup group) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.eq("id", group.getId()))
                .createAlias("authorsOf", "game").
                setProjection(Projections.avg("game.totalRating"));

        Object object = criteria.uniqueResult();
        if(object == null) {
            return 0;
        } else {
            return ((Double)object).intValue();
        }
    }

    @SuppressWarnings("unchecked")
    public List<CsldGroup> getFirstChoices(String startsWith, int maxChoices) {
        Session session = sessionFactory.getCurrentSession();
        Criteria query = getBuilder().build().getExecutableCriteria(session)
                .setMaxResults(maxChoices)
                .add(Restrictions.ilike("name","%"+startsWith+"%"));

        return query.list();
    }
}
