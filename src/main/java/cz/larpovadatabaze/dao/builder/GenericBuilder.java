package cz.larpovadatabaze.dao.builder;

import org.hibernate.criterion.DetachedCriteria;

/**
 *
 */
public class GenericBuilder<T> implements IBuilder {
    protected DetachedCriteria baseCriteria;

    public GenericBuilder(Class<T> clazz){
        this.baseCriteria = DetachedCriteria.forClass(clazz);
    }

    @Override
    public DetachedCriteria build() {
        return baseCriteria;
    }
}
