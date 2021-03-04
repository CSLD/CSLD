package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.users.services.CsldUsers;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigFetcher implements DataFetcher<Map<String, Object>> {
    private final CsldUsers csldUsers;

    @Autowired
    public ConfigFetcher(CsldUsers csldUsers) {
        this.csldUsers = csldUsers;
    }

    @Override
    public Map<String, Object> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Map<String, Object> res = new HashMap<>();

        res.put("reCaptchaKey", csldUsers.getReCaptchaSiteKey());

        return res;
    }
}
