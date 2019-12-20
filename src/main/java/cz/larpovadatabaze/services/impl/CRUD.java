package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.services.CRUDService;

import java.io.Serializable;
import java.util.List;

/**
 * Implementation of the basic CRUD operations necessary for working with most of the entities.
 *
 * @param <T> Entity which takes part in the standard CRUD
 * @param <I> Identity for entity.
 */
public class CRUD<T, I extends Serializable> implements CRUDService<T, I> {
    protected GenericHibernateDAO<T, I> crudRepository;

    public CRUD(GenericHibernateDAO<T, I> crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public List<T> getAll() {
        return crudRepository.findAll();
    }

    @Override
    public List<T> getUnique(T example) {
        return crudRepository.findByExample(example);
    }

    @Override
    public T getById(I id) {
        return crudRepository.findById(id);
    }

    @Override
    public void remove(T toRemove) {
        crudRepository.makeTransient(toRemove);
    }

    @Override
    public List<T> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("By default does not support autocompletion");
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        return crudRepository.saveOrUpdate(entity);
    }
}
