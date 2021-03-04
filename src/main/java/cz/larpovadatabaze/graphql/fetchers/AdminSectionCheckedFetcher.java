package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.users.services.AppUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checker that checks whether user is admin
 */
@Component
public class AdminSectionCheckedFetcher implements DataFetcher<Object> {
    private final AppUsers appUsers;

    @Autowired
    public AdminSectionCheckedFetcher(AppUsers appUsers) {
        this.appUsers = appUsers;
    }

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        if (!appUsers.isAtLeastEditor()) {
            throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Admins and editors only");
        }

        return new Object();
    }
}
