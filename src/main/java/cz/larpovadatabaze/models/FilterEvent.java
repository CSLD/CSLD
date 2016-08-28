package cz.larpovadatabaze.models;

import cz.larpovadatabaze.entities.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FilterEvent implements Serializable {
    private List<Label> requiredLabels = new ArrayList<Label>();
    private List<Label> otherLabels = new ArrayList<Label>();
    private boolean showOnlyFuture = true;

    public List<Label> getRequiredLabels() {
        return requiredLabels;
    }

    public void setRequiredLabels(List<Label> requiredLabels) {
        this.requiredLabels = requiredLabels;
    }

    public List<Label> getOtherLabels() {
        return otherLabels;
    }

    public void setOtherLabels(List<Label> otherLabels) {
        this.otherLabels = otherLabels;
    }

    public boolean isShowOnlyFuture() {
        return showOnlyFuture;
    }

    public void setShowOnlyFuture(boolean showOnlyFuture) {
        this.showOnlyFuture = showOnlyFuture;
    }
}
