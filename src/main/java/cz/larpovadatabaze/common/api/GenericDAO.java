package cz.larpovadatabaze.common.api;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.List;

/**
 * Based on http://community.jboss.org/wiki/GenericDataAccessObjects
 *
 * @author jmader
 */
public interface GenericDAO<T, I extends Serializable> {
    /**
     * Works only with identifiers specified by Id. Do not use this method, if you expect
     * entity to be nonexistent.
     *
     * @param id id of the object.
     * @return Existing object
     */
    T findById(I id);

    /**
     * Returns all entities in the given store. Use carefully as there could be severe performance issues.
     *
     * @return List of all entities in the store.
     */
    List<T> findAll();

    /**
     * Returns all entities which have the same properties as the one provided. It is possible to supply which properties
     * to ignore.
     *
     * @param exampleInstance Instance to model the query after.
     * @param excludeProperty Array of names of properties which should be ignored.
     * @return List of entities similar to the example instance.
     */
    List<T> findByExample(T exampleInstance, String[] excludeProperty);

    boolean saveOrUpdate(T entity);

    /**
     * Removes the entity from the store.
     *
     * @param entity Entity to remove.
     */
    void delete(T entity);

    List<T> findByCriteria(Criterion... criterion);

    Criteria getExecutableCriteria();

    /**
     * Returns current session. Use sparingly.
     *
     * @return Current session
     */
    Session getCurrentSession();
}
