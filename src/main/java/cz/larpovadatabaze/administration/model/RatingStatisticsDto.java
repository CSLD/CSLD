package cz.larpovadatabaze.administration.model;

import java.io.Serializable;

public class RatingStatisticsDto implements Serializable {
    Object year;
    Object month;
    Object average_rating;
    Object amount;

    public Object getAmount() {
        return amount;
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }

    public Object getYear() {
        return year;
    }

    public void setYear(Object year) {
        this.year = year;
    }

    public Object getMonth() {
        return month;
    }

    public void setMonth(Object month) {
        this.month = month;
    }

    public Object getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Object average_rating) {
        this.average_rating = average_rating;
    }

    public Object getNumRatings() { return amount; }

    public Object getAverageRating() { return average_rating; }
}
