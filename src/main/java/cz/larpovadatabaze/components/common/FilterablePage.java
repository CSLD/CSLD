package cz.larpovadatabaze.components.common;

/**
 * Created by jbalhar on 28. 8. 2016.
 */
public interface FilterablePage {
    public void filterChanged(boolean sortChanged, boolean requiredLabelsChanged, boolean otherLabelsChanged);
}
