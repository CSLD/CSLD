package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.SimilarGame;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.services.SimilarGames;

import java.util.List;

public class InMemorySimilarGames extends InMemoryCrud<SimilarGame, Integer> implements SimilarGames {
    @Override
    public List<Game> allForGame(Game game) {
        return null;
    }

    @Override
    public void recalculateForAll() {

    }
}
