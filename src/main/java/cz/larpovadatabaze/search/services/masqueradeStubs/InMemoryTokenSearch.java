package cz.larpovadatabaze.search.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.TokenSearch;

import java.util.List;

public class InMemoryTokenSearch implements TokenSearch {
    @Override
    public List<Label> findLabels(String token) {
        return null;
    }

    @Override
    public List<Game> findGames(String token) {
        return null;
    }

    @Override
    public List<CsldUser> findUsers(String token) {
        return null;
    }

    @Override
    public List<CsldGroup> findGroups(String token) {
        return null;
    }
}
