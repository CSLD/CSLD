package cz.larpovadatabaze.models;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 3.5.13
 * Time: 12:47
 */
public class FilterGame implements Serializable {
    private Integer minHours;
    private Integer maxHours;
    private Integer minDays;
    private Integer maxDays;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Boolean labelCheck;

    public Integer getMinHours() {
        return minHours;
    }

    public void setMinHours(Integer minHours) {
        this.minHours = minHours;
    }

    public Integer getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(Integer maxHours) {
        this.maxHours = maxHours;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public void setMinDays(Integer minDays) {
        this.minDays = minDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Boolean getLabelCheck() {
        return labelCheck;
    }

    public void setLabelCheck(Boolean labelCheck) {
        this.labelCheck = labelCheck;
    }
}
