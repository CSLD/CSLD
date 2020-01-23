package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.AuthoredGames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryAuthoredGames implements AuthoredGames {
    @Override
    public List<Game> gamesOfAuthors(Game game) {
        return null;
    }

    @Override
    public Collection<Game> getGamesOfAuthor(CsldUser author, int offset, int limit) {
        return new ArrayList<>();
    }

    @Override
    public long getAmountOfGamesOfAuthor(CsldUser author) {
        return 0;
    }

    @Override
    public Collection<Game> getGamesOfGroup(CsldGroup groupOfAuthors, int offset, int limit) {
        return new ArrayList<>();
    }

    @Override
    public long getAmountOfGamesOfGroup(CsldGroup groupOfAuthors) {
        return 0;
    }
}
