package cz.larpovadatabaze.common.dao.builder;

import org.hibernate.criterion.DetachedCriteria;

/**
 * Every builder must provide build method returning built basic Criteria for given type. This builder
 * is used everywhere when retrieving any data at all.
 * This object works in way similar to filters, It is called before any other modification on criteria is made
 * and prepares default criteria.
 * Example usage is game and deleted games. They are shown only to editors and administrators and it
 * is implemented via usage of Builder.
 */
public interface IBuilder {
    /**
     * Prepare general criteria with default values, unless stated otherwise on specific Builder.
     *
     * @return Criteria to be used in all queries.
     */
    DetachedCriteria build();

    Class getClassSpecific();
}
