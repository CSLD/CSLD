package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 18:26
 */
@Repository
public class GroupDAO extends GenericHibernateDAO<CsldGroup, Integer> {
    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<CsldGroup> orderedByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from CsldGroup order by name");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param groupName Expected format is {Name} Name is unique identifier of group.
     * @return It should return only single group or no group if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException {
        Criteria uniqueGroup = sessionFactory.getCurrentSession().createCriteria(CsldGroup.class).add(
                Restrictions.eq("name", groupName)
        );
        return uniqueGroup.list();
    }

    public int getAmountOfGroups() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CsldGroup.class);
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    public int getAverageOfGroup(CsldGroup group) {
        Session session = sessionFactory.getCurrentSession();
        String sqlForAverage = String.format("select sum(game.total_rating)/count(*) from csld_game as game " +
                "join csld_game_has_group ghg on game.id = ghg.id_game where ghg.id_group = %s",group.getId());
        Query query = session.createSQLQuery(sqlForAverage);
        Object object = query.uniqueResult();
        if(object == null) {
            return 0;
        } else {
            return ((Double)object).intValue();
        }
    }

    public List<CsldGroup> getFirstChoices(String startsWith, int maxChoices) {
        Session session = sessionFactory.getCurrentSession();
        Criteria query = session.createCriteria(CsldGroup.class);
        query.setMaxResults(maxChoices);
        query.add(Restrictions.ilike("name","%"+startsWith+"%"));
        return query.list();
    }
}
