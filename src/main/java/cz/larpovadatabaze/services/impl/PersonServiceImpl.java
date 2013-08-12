package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.PersonDAO;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.PersonService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 12.4.13
 * Time: 9:53
 */
@Repository
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonDAO personDAO;

    @Override
    public void saveOrUpdate(Person person) {
        personDAO.saveOrUpdate(person);
        personDAO.flush();
    }

    @Override
    public List<Person> getAll() {
        return personDAO.findAll();
    }

    @Override
    public List<Person> getUnique(Person example) {
        List<Person> uniqueResult = personDAO.findByExample(example, new String[]{});
        personDAO.flush();
        return uniqueResult;
    }

    @Override
    public Person getByEmail(String email) {
        Criterion criterion = Restrictions.eq("form.email", email);
        Person uniqueResult = personDAO.findSingleByCriteria(criterion);
        personDAO.flush();
        return uniqueResult;
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param autoCompletable Expected format is {Name, Email} Email is unique identifier of person.
     * @return It should return only single person or no person if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Person> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        return personDAO.getByAutoCompletable(autoCompletable);
    }

    @Override
    public void remove(Person toRemove) {
        personDAO.makeTransient(toRemove);
    }
}
