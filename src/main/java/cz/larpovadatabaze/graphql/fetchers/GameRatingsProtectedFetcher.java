package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Collection;
import java.util.Collections;

public class GameRatingsProtectedFetcher implements DataFetcher<Collection<Rating>> {
    @Override
    public Collection<Rating> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        if (!CsldAuthenticatedWebSession.get().isAtLeastEditor()) {
            // Regular users do not see ratings
            return Collections.emptyList();
        }

        Game game = dataFetchingEnvironment.getSource();
        return game.getRatings();
    }
}
