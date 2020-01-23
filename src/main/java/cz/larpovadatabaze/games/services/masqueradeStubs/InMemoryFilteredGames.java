package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.models.FilterGame;
import cz.larpovadatabaze.games.services.FilteredGames;

import java.util.List;

public class InMemoryFilteredGames implements FilteredGames {
    @Override
    public List<Game> paginated(FilterGame filter, int offset, int limit) {
        return null;
    }

    @Override
    public long totalAmount(FilterGame filter) {
        return 0;
    }
}
