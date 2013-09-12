package cz.larpovadatabaze.api;

import org.apache.wicket.model.IModel;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 18:16
 */
public abstract class AbstractEntityModel<T extends Identifiable< ? >> implements IModel<T>
{
    private final Class clazz;
    private Serializable id;

    private T entity;

    public AbstractEntityModel(T entity)
    {
        clazz = entity.getClass();
        id = entity.getId();
        this.entity = entity;
    }

    public AbstractEntityModel(Class< ? extends T> clazz, Serializable id)
    {
        this.clazz = clazz;
        this.id = id;
    }

    public T getObject()
    {
        if (entity == null)
        {
            if (id != null)
            {
                entity = load(clazz, id);
                if (entity == null)
                {
                    throw new EntityNotFoundException();
                }
            }
        }
        return entity;
    }

    public void detach()
    {
        if (entity != null)
        {
            if (entity.getId() != null)
            {
                id = entity.getId();
                entity = null;
            }
        }
    }

    protected abstract T load(Class clazz, Serializable id);

    public void setObject(T object)
    {
        throw new UnsupportedOperationException(getClass() + " does not support #setObject(T entity)");
    }
}
