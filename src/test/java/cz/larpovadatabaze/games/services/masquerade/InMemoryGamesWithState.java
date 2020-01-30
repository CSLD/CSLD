package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;
import cz.larpovadatabaze.games.services.GamesWithState;

import java.util.List;

public class InMemoryGamesWithState implements GamesWithState {
    @Override
    public List<IGameWithRating> getPlayedByUser(CsldUser user) {
        return null;
    }

    @Override
    public List<Game> getWantedByUser(CsldUser user) {
        return null;
    }

    @Override
    public long getAmountOfGamesPlayedBy(CsldUser user) {
        return 0;
    }

    @Override
    public void sendEmailToInterested(Game game, String url) {

    }
}
