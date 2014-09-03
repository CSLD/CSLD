package cz.larpovadatabaze.services;

import java.util.Collection;

/**
 * Filtering capabilities for Collections
 */
public interface FilterService {
    /**
     * It goes through every item in the collection and if it has property with given name and given value it inserts
     * this item to the collection to be returned.
     *
     * @param toFilter Collection which will be filtered.
     * @param propertyName Name of the property on which we call filter.
     * @param propertyValue Value of the property, which should be inserted to the resulting collection.
     * @return New collection containing only values relevant to the filter.
     */
    public Collection filterByPropertyName(Collection toFilter, String propertyName, Object propertyValue);
}
