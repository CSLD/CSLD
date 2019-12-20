package cz.larpovadatabaze.api;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.List;

/**
 * Based on http://community.jboss.org/wiki/GenericDataAccessObjects
 * 
 * @author jmader
 *
 */
public interface GenericDAO<T, I extends Serializable> {

    T findById(I id);

    List<T> findAll();

    List<T> findByExample(T exampleInstance, String[] excludeProperty);

    T makePersistent(T entity);

    void makeTransient(T entity);

    List<T> findByCriteria(Criterion... criterion);

    Criteria getExecutableCriteria();

    Session getCurrentSession();
}
