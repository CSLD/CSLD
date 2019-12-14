package cz.larpovadatabaze.dao.builder;

import org.hibernate.criterion.DetachedCriteria;

/**
 *
 */
public class GenericBuilder<T> implements IBuilder {
    protected DetachedCriteria baseCriteria;
    protected Class<T> clazz;

    public GenericBuilder(Class<T> clazz){
        this.baseCriteria = DetachedCriteria.forClass(clazz);
        this.clazz = clazz;
    }

    @Override
    public DetachedCriteria build() {
        return baseCriteria;
    }

    public Class<T> getClassSpecific() {
        return this.clazz;
    }
}
