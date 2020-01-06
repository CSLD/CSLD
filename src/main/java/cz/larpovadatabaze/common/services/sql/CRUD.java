package cz.larpovadatabaze.common.services.sql;

import cz.larpovadatabaze.common.api.GenericHibernateDAO;
import cz.larpovadatabaze.common.services.CRUDService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Implementation of the basic CRUD operations necessary for working with most of the entities.
 *
 * @param <T> Entity which takes part in the standard CRUD
 * @param <I> Identity for entity.
 */
@Repository
@Transactional
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
        crudRepository.delete(toRemove);
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
