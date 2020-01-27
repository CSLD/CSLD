package cz.larpovadatabaze.common.dao;

import cz.larpovadatabaze.common.dao.builder.IBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.List;

public class GenericHibernateDAO<T, I extends Serializable>
        implements GenericDAO<T, I> {

    protected SessionFactory sessionFactory;
    protected IBuilder builder;

    public GenericHibernateDAO(SessionFactory sessionFactory, IBuilder builder) {
        this.sessionFactory = sessionFactory;
        this.builder = builder;
    }

    public IBuilder getBuilder() {
        return builder;
    }

   public T findById(I id) {
       return findSingleByCriteria(Restrictions.eq("id", id));
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

    @SuppressWarnings("unchecked")
    public List<T> findByExecutableCriteria(Criteria criteria) {
        return criteria.list();
    }

    @Override
    public Criteria getExecutableCriteria() {
        return getBuilder().build().getExecutableCriteria(getCurrentSession());
    }

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
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

    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
        sessionFactory.getCurrentSession().flush();
    }

    public boolean saveOrUpdate(T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return true;
    }

    public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void clear() {
		sessionFactory.getCurrentSession().clear();
	}
}
