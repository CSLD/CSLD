package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabelFetcherFactory {
    private final Labels sqlLabels;
    private final AppUsers appUsers;
    private final CsldUsers users;

    @Autowired
    public LabelFetcherFactory(Labels sqlLabels, AppUsers appUsers, CsldUsers users) {
        this.sqlLabels = sqlLabels;
        this.appUsers = appUsers;
        this.users = users;
    }

    public DataFetcher<List<Label>> createAuthorizedRequiredLabelsFetcher() {
        return new DataFetcher<List<Label>>() {
            @Override
            public List<Label> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return sqlLabels.getAuthorizedRequired(appUsers.getLoggedUser());
            }
        };
    }

    public DataFetcher<List<Label>> createAuthorizedOptionalLabelsFetcher() {
        return new DataFetcher<List<Label>>() {
            @Override
            public List<Label> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return sqlLabels.getAuthorizedOptional(appUsers.getLoggedUser());
            }
        };
    }

    public DataFetcher<List<Label>> createAllLabelsFetcher() {
        return new DataFetcher<List<Label>>() {
            @Override
            public List<Label> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return sqlLabels.getAll();
            }
        };
    }

}
