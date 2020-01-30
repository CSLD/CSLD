package cz.larpovadatabaze.administration.components.page;

import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

@AuthorizeInstantiation({"Editor", "Admin"})
public class RatingStatistics extends CsldBasePage {
    @SpringBean
    Statistics statistics;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<RatingStatisticsDto> ratingStats = statistics.amountAndRatingsPerMonth();
        ListView<RatingStatisticsDto> userRatesOwnGameDtoListView = new ListView<>("ratingStatistics", ratingStats) {
            @Override
            protected void populateItem(ListItem<RatingStatisticsDto> item) {
                RatingStatisticsDto ratingStats = item.getModel().getObject();

                item.add(new Label("year", ratingStats.getYear().toString()));
                item.add(new Label("month", ratingStats.getMonth().toString()));
                item.add(new Label("amountOfRatings", ratingStats.getAmount().toString()));
                item.add(new Label("averageRating", ratingStats.getAverage_rating().toString()));
            }
        };

        add(userRatesOwnGameDtoListView);
    }
}
