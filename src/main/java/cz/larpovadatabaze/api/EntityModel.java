package cz.larpovadatabaze.api;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public class EntityModel<T extends Identifiable<?>> extends AbstractEntityModel<T>
{
    @Autowired
    private SessionFactory sessionFactory;

    public EntityModel(T entity) {
        super(entity);
    }

    public EntityModel(Class< ? extends T> clazz, Serializable id)
    {
        super(clazz, id);
    }


    @Override
    protected T load(Class clazz, Serializable id)
    {
        return (T) sessionFactory.getCurrentSession().get(clazz, id);
    }
}
