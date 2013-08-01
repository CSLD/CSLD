package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class LabelDAO extends GenericHibernateDAO<Label, Integer> {
    /**
     * Used when autoCompletable field is used.
     *
     * @param labelName Expected format is {Name} Name is unique identifier of label.
     * @return It should return only single label or no label if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Label> getByAutoCompletable(String labelName) throws WrongParameterException {
        Criteria uniqueLabel = sessionFactory.getCurrentSession().createCriteria(Label.class).add(
                Restrictions.eq("name", labelName)
        );
        return uniqueLabel.list();
    }
}
