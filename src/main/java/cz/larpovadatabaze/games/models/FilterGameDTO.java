package cz.larpovadatabaze.games.models;

import cz.larpovadatabaze.common.entities.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FilterGameDTO implements Serializable {
    public enum OrderBy {
        NUM_RATINGS_DESC,
        ADDED_DESC,
        RATING_DESC,
        NUM_COMMENTS_DESC
    }

    private List<Label> requiredLabels = new ArrayList<Label>();
    private List<Label> otherLabels = new ArrayList<Label>();

    /**
     * The games with activity more recent than 2 years.
     */
    private boolean showOnlyNew = false;
    private boolean showArchived = true;

    private OrderBy orderBy = OrderBy.NUM_RATINGS_DESC;

    public FilterGameDTO() {
    }

    public FilterGameDTO(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public FilterGameDTO(List<Label> requiredLabels) {
        this.requiredLabels = requiredLabels;
    }

    public FilterGameDTO(FilterGameDTO filterToCopy, OrderBy orderBy, boolean showOnlyNew) {
        this.orderBy = orderBy;
        this.showOnlyNew = showOnlyNew;

        this.requiredLabels = filterToCopy.getRequiredLabels();
        this.otherLabels = filterToCopy.getOtherLabels();

        this.showArchived = filterToCopy.showArchived;

    }

    public List<Label> getRequiredLabels() {
        return requiredLabels;
    }

    public List<Label> getOtherLabels() {
        return otherLabels;
    }

    public boolean isShowOnlyNew() {
        return showOnlyNew;
    }
    public boolean isShowArchived() {
        return showArchived;
    }
    public OrderBy getOrderBy() {
        return orderBy;
    }
}
