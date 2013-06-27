package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.Person;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 12.4.13
 * Time: 9:52
 */
@Repository
public class PersonDAO extends GenericHibernateDAO<Person, Integer> {
}
