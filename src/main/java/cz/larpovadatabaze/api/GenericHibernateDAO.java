package cz.larpovadatabaze.api;

import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Language;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericHibernateDAO<T, ID extends Serializable>
		implements GenericDAO<T, ID> {
    @Autowired
	protected SessionFactory sessionFactory;

	public GenericHibernateDAO() {}

	public abstract IBuilder getBuilder();

    /**
     * Works only with identifiers specified by Id. Do not use this method, if you expect
     * entity to be nonexistent.
     *
     * @param id id of the object.
     * @return Existing object
     */
	@SuppressWarnings("unchecked")
	public T findById(ID id) {
		return findSingleByCriteria(Restrictions.eq("id",id));
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return findByCriteria();
	}

    public List<T> findByExample(T exampleInstance) {
        return findByExample(exampleInstance, new String[]{});
    }

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit.list();
	}

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    public T findSingleByCriteria(Criterion... criterion) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (T) crit.uniqueResult();
    }

	@SuppressWarnings("unchecked")
	public T makePersistent(T entity) {
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
		return entity;
	}

	public void makeTransient(T entity) {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(entity);
        sessionFactory.getCurrentSession().flush();
        tx.commit();
	}

    public boolean saveOrUpdate(T entity) {
        try {
            sessionFactory.getCurrentSession().merge(entity);
            flush();
            return true;
        } catch (HibernateException ex){
            ex.printStackTrace();
        }

        try{
            sessionFactory.getCurrentSession().saveOrUpdate(entity);
            flush();
            return true;
        } catch (HibernateException ex){
            ex.printStackTrace();
            return false;
        }
    }

	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void clear() {
		sessionFactory.getCurrentSession().clear();
	}
}
