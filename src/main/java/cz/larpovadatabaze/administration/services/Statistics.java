package cz.larpovadatabaze.administration.services;

import cz.larpovadatabaze.administration.model.RatingStatisticsDto;

import java.util.List;

public interface Statistics {
    /**
     * Return aggregated information for every month since the start of the database.
     * Show amount of ratings and average of ratings for every month.
     *
     * @return Monthly stats on amount and average of ratings.
     */
    List<RatingStatisticsDto> amountAndRatingsPerMonth();
}
