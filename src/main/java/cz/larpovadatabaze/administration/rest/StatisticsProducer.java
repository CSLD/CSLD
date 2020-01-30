package cz.larpovadatabaze.administration.rest;

import com.google.gson.Gson;
import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.services.Statistics;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.IOException;
import java.util.List;

public class StatisticsProducer extends AbstractResource {
    private Statistics statistics;

    public StatisticsProducer(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        List<RatingStatisticsDto> ratingStatistics = statistics.amountAndRatingsPerMonth();

        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("application/json");
        resourceResponse.setTextEncoding("utf-8");

        String statisticsInJson = new Gson().toJson(ratingStatistics);
        resourceResponse.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                attributes.getResponse().write(statisticsInJson);
            }
        });

        return resourceResponse;
    }
}
