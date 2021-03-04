package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminMutationFetcherFactory {
    private final Labels labels;
    private final CsldUsers users;
    private final AppUsers appUsers;

    @Autowired
    public AdminMutationFetcherFactory(Labels labels, CsldUsers users, AppUsers appUsers) {
        this.labels = labels;
        this.users = users;
        this.appUsers = appUsers;
    }

    private Label safeGetLabel(String id) {
        Label label = labels.getById(Integer.parseInt(id));
        if (label == null) {
            throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "Label not found");
        }
        return label;
    }

    private CsldUser safeGetUserAndCheckIsAdmin(String id) {
        if (!appUsers.isAdmin()) {
            throw new GraphQLException(GraphQLException.ErrorCode.ACCESS_DENIED, "Admins only");
        }

        CsldUser user = users.getById(Integer.parseInt(id));
        if (user == null) {
            throw new GraphQLException(GraphQLException.ErrorCode.NOT_FOUND, "User not found");
        }
        return user;
    }

    public DataFetcher<Label> createUpdateLabelFetcher() {
        return new DataFetcher<Label>() {
            @Override
            public Label get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Map<String, Object> input = dataFetchingEnvironment.getArgument("input");

                Label label = safeGetLabel((String)input.get("id"));
                label.setName((String)input.get("name"));
                label.setDescription((String)input.get("description"));

                labels.saveOrUpdate(label);

                return label;
            }
        };
    }

    public DataFetcher<Label> createSetLabelRequiredFetcher() {
        return new DataFetcher<Label>() {
            @Override
            public Label get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Label label = safeGetLabel(dataFetchingEnvironment.getArgument("labelId"));
                label.setRequired(dataFetchingEnvironment.getArgument("required"));

                labels.saveOrUpdate(label);

                return label;
            }
        };
    }

    public DataFetcher<Label> createSetLabelAuthorizedFetcher() {
        return new DataFetcher<Label>() {
            @Override
            public Label get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Label label = safeGetLabel(dataFetchingEnvironment.getArgument("labelId"));
                label.setAuthorized(dataFetchingEnvironment.getArgument("authorized"));

                labels.saveOrUpdate(label);

                return label;
            }
        };
    }

    public DataFetcher<Label> createDeleteLabelFetcher() {
        return new DataFetcher<Label>() {
            @Override
            public Label get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Label label = safeGetLabel(dataFetchingEnvironment.getArgument("labelId"));

                labels.remove(label);

                return label;
            }
        };
    }

    public DataFetcher<CsldUser> createSetUserRoleFetcher() {
        return new DataFetcher<CsldUser>() {
            @Override
            public CsldUser get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                CsldUser user = safeGetUserAndCheckIsAdmin(dataFetchingEnvironment.getArgument("userId"));

                user.setRole(CsldRoles.getRoleByName(dataFetchingEnvironment.getArgument("role").toString().toLowerCase()));

                users.saveOrUpdate(user);

                return user;
            }
        };
    }

    public DataFetcher<CsldUser> createDeleteUserFetcher() {
        return new DataFetcher<CsldUser>() {
            @Override
            public CsldUser get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                CsldUser user = safeGetUserAndCheckIsAdmin(dataFetchingEnvironment.getArgument("userId"));

                users.remove(user);

                return user;
            }
        };
    }
}
