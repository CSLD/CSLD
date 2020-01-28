package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import cz.larpovadatabaze.games.services.FilteredGames;

import java.util.List;

public class InMemoryFilteredGames implements FilteredGames {
    @Override
    public List<Game> paginated(FilterGameDTO filter, int offset, int limit) {
        return null;
    }

    @Override
    public long totalAmount(FilterGameDTO filter) {
        return 0;
    }
}
