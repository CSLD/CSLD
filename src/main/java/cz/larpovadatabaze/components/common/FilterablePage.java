package cz.larpovadatabaze.components.common;

/**
 * Interface necessary to implement for Ajax enabled filtering.
 */
public interface FilterablePage {
    void filterChanged(boolean sortChanged, boolean requiredLabelsChanged, boolean otherLabelsChanged);
}
