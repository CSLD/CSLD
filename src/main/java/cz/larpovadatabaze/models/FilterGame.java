package cz.larpovadatabaze.models;

import java.io.Serializable;

/**
 *
 */
public class FilterGame implements Serializable {
    private Double minHours = 0d;
    private Double maxHours;
    private Double minDays = 0d;
    private Double maxDays;
    private Double minPlayers = 0d;
    private Double maxPlayers;

    public Double getMinHours() {
        return minHours;
    }

    public void setMinHours(Double minHours) {
        this.minHours = minHours;
    }

    public Double getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(Double maxHours) {
        this.maxHours = maxHours;
    }

    public Double getMinDays() {
        return minDays;
    }

    public void setMinDays(Double minDays) {
        this.minDays = minDays;
    }

    public Double getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(Double maxDays) {
        this.maxDays = maxDays;
    }

    public Double getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Double minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Double getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Double maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
