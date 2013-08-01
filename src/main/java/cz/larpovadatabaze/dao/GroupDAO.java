package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public List<CsldGroup> orderedByName() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from CsldGroup CsldGroup order by CsldGroup.name");
        List<CsldGroup> allGroups = query.list();
        return allGroups;
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
}
