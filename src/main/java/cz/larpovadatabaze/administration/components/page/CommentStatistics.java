package cz.larpovadatabaze.administration.components.page;

import cz.larpovadatabaze.administration.model.MonthlyAmountsStatisticsDto;
import cz.larpovadatabaze.administration.services.Statistics;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

@AuthorizeInstantiation({"Editor", "Admin"})
public class CommentStatistics extends CsldBasePage {
    @SpringBean
    Statistics statistics;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<MonthlyAmountsStatisticsDto> ratingStats = statistics.amountOfCommentsPerMonth();
        ListView<MonthlyAmountsStatisticsDto> userRatesOwnGameDtoListView = new ListView<>("commentsStatistics", ratingStats) {
            @Override
            protected void populateItem(ListItem<MonthlyAmountsStatisticsDto> item) {
                MonthlyAmountsStatisticsDto ratingStats = item.getModel().getObject();

                item.add(new Label("year", ratingStats.year.toString()));
                item.add(new Label("month", ratingStats.month.toString()));
                item.add(new Label("amountOfComments", ratingStats.amount.toString()));
            }
        };

        add(userRatesOwnGameDtoListView);
    }
}
