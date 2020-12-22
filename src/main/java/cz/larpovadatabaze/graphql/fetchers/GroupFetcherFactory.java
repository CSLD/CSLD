package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldGroups;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GroupFetcherFactory {
    private final CsldGroups csldGroups;
    private final AppUsers appUsers;

    @Autowired
    public GroupFetcherFactory(CsldGroups csldGroups, AppUsers appUsers) {
        this.csldGroups = csldGroups;
        this.appUsers = appUsers;
    }

    public DataFetcher<CsldGroup> createGroupByIdFetcher() {
        return dataFetchingEnvironment -> {
            Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("groupId"));
            return csldGroups.getById(id);
        };
    }

    public DataFetcher<CsldGroup> createCreateGroupFetcher() {
        return dataFetchingEnvironment -> {
            if (!appUsers.isAtLeastEditor()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "At least editor needed");
            }

            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            CsldGroup newGroup = new CsldGroup();
            newGroup.setName((String)input.get("name"));

            csldGroups.saveOrUpdate(newGroup);

            return newGroup;
        };
    }

    public DataFetcher<CsldGroup> createUpdateGroupFetcher() {
        return dataFetchingEnvironment -> {
            if (!appUsers.isAtLeastEditor()) {
                throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "At least editor needed");
            }

            Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

            Integer id = Integer.parseInt((String)input.get("id"));
            CsldGroup group = csldGroups.getById(id);
            group.setName((String)input.get("name"));

            csldGroups.saveOrUpdate(group);

            return group;
        };
    }
}
