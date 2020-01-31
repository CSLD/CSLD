package cz.larpovadatabaze.administration.services.masquerade;

import cz.larpovadatabaze.administration.model.MonthlyAmountsStatisticsDto;
import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.services.Statistics;

import java.util.List;

public class InMemoryStatistics implements Statistics {
    @Override
    public List<RatingStatisticsDto> amountAndRatingsPerMonth() {
        return null;
    }

    @Override
    public List<MonthlyAmountsStatisticsDto> amountOfCommentsPerMonth() {
        return null;
    }
}
