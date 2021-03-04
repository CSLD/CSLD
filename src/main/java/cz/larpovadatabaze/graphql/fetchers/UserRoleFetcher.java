package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.CsldRoles;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class UserRoleFetcher implements DataFetcher<String> {
    @Override
    public String get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        CsldUser user = dataFetchingEnvironment.getSource();

        short role = user.getRole();
        for(CsldRoles csldRole: CsldRoles.values()) {
            if (csldRole.getRole() == role) {
                return csldRole.getRoleName().toUpperCase();
            }
        }

        return CsldRoles.ANONYMOUS.getRoleName().toUpperCase();
    }
}
