package cz.larpovadatabaze.models;

import cz.larpovadatabaze.calendar.service.Area;
import cz.larpovadatabaze.calendar.service.GeographicalFilter;
import cz.larpovadatabaze.entities.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class FilterEvent implements Serializable {
    private List<Label> requiredLabels = new ArrayList<Label>();
    private List<Label> otherLabels = new ArrayList<Label>();
    private Date from;
    private Date to;
    private Area region;
    private GeographicalFilter filter;
    private Sort sorted;

    public GeographicalFilter getFilter() {
        return filter;
    }

    public FilterEvent() {

    }

    public FilterEvent(GeographicalFilter geographicalFilter) {
        filter = geographicalFilter;

    }

    public Area getRegion() {
        return region;
    }

    public void setRegion(Area region) {
        this.region = region;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    private Integer limit;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

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

    public Sort getSorted() {
        return sorted;
    }

    public void setSorted(Sort sorted) {
        this.sorted = sorted;
    }

    public enum Sort {
        TIME_MOST_RECENT
    }
}
