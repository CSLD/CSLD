package cz.larpovadatabaze.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.larpovadatabaze.entities.Label;

/**
 *
 */
public class FilterGame implements Serializable {

    public static enum OrderBy {
        NUM_RATINGS_DESC,
        ADDED_DESC,
        RATING_DESC,
        NUM_COMMENTS_DESC
    }

    private List<Label> requiredLabels = new ArrayList<Label>();
    private List<Label> otherLabels = new ArrayList<Label>();
    private List<Locale> languages = new ArrayList<Locale>();
    private boolean showOnlyNew;
    private boolean showArchived;
    private OrderBy orderBy = OrderBy.NUM_RATINGS_DESC;

    public List<Label> getRequiredLabels() {
        return requiredLabels;
    }

    public List<Label> getOtherLabels() {
        return otherLabels;
    }

    public List<Locale> getLanguages() {
        return languages;
    }

    public boolean isShowOnlyNew() {
        return showOnlyNew;
    }

    public void setShowOnlyNew(boolean showOnlyNew) {
        this.showOnlyNew = showOnlyNew;
    }

    public boolean isShowArchived() {
        return showArchived;
    }

    public void setShowArchived(boolean showArchived) {
        this.showArchived = showArchived;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}
