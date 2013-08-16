package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 12.4.13
 * Time: 9:53
 */
public interface PersonService extends GenericService<Person> {

    public boolean saveOrUpdate(Person person);

    List<Person> getAll();

    Person getByEmail(String email);

    List<Person> getByAutoCompletable(String autoCompletable) throws WrongParameterException;
}
