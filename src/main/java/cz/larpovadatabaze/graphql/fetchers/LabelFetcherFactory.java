package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.games.services.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LabelFetcherFactory {
    private final Labels sqlLabels;

    @Autowired
    public LabelFetcherFactory(Labels sqlLabels) {
        this.sqlLabels = sqlLabels;
    }
}
