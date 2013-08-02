package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Person is AutoCompletable and therefore this dao contains method for working with IAutoCompletable
 */
@Repository
public class PersonDAO extends GenericHibernateDAO<Person, Integer> {
    /**
     * Used when autoCompletable field is used.
     *
     * @param autoCompletable Expected format is {Name, Email} Email is unique identifier of person.
     * @return It should return only single person or no person if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Person> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        String[] personData = autoCompletable.split(", ");
        if(personData.length < 2) {
            throw new WrongParameterException();
        }
        String email = personData[1];
        Criteria uniquePerson = sessionFactory.getCurrentSession().createCriteria(Person.class).add(
                Restrictions.eq("email",email)
        );
        return uniquePerson.list();
    }
}
