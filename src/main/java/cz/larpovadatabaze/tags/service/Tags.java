package cz.larpovadatabaze.tags.service;

import cz.larpovadatabaze.entities.Label;

import java.util.Collection;

/**
 * It represents possibilities for manipulation of labels or tags.
 */
public interface Tags {
    /**
     * It returns all labels in given store.
     * @return All events in the data source.
     */
    Collection<Label> all();
}
