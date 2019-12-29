package cz.larpovadatabaze.common.components;

/**
 * Interface necessary to implement for Ajax enabled filtering.
 */
public interface FilterablePage {
    void filterChanged(boolean sortChanged, boolean requiredLabelsChanged, boolean otherLabelsChanged);
}
