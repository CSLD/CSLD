package cz.larpovadatabaze.dao.builder;

import org.hibernate.criterion.DetachedCriteria;

/**
 *
 */
public class GenericBuilder<T> implements IBuilder {
    protected Class<T> clazz;

    public GenericBuilder(Class<T> clazz){
        this.clazz = clazz;
    }

    @Override
    public DetachedCriteria build() {
        return DetachedCriteria.forClass(clazz);
    }

    public Class<T> getClassSpecific() {
        return this.clazz;
    }
}
