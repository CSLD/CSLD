package cz.larpovadatabaze.services;

import java.util.List;

/**
 * Necessary minimum every CRUD Service must be able to handle.
 */
public interface CRUDService<T, I> {
    /**
     * Returns all entities in the given service. Should be used with care.
     *
     * @return List of all entities of given type.
     */
    List<T> getAll();

    /**
     * @param example
     * @return
     */
    List<T> getUnique(T example);

    T getById(I id);

    void remove(T toRemove);

    List<T> getFirstChoices(String startsWith, int maxChoices);

    boolean saveOrUpdate(T entity);
}
