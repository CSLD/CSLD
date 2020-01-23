package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.models.FilterGame;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.Collection;
import java.util.List;

public class InMemoryGames extends InMemoryCrud<Game, Integer> implements Games {
    @Override
    public void evictGame(Integer id) {
    }

    @Override
    public boolean addGame(Game game) {
        return saveOrUpdate(game);
    }

    @Override
    public List<Game> getSimilar(Game game) {
        return inMemory.subList(0, 5);
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        return inMemory.subList(0, 5);
    }

    @Override
    public List<Game> getLastGames(int amountOfGames) {
        return inMemory.subList(0, 5);
    }

    @Override
    public List<Game> getFilteredGames(FilterGame filterGame, int offset, int limit) {
        return inMemory.subList(0, limit);
    }

    @Override
    public long getAmountOfFilteredGames(FilterGame filterGame) {
        return inMemory.size();
    }

    @Override
    public Collection<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        return inMemory.subList(0, 5);
    }

    @Override
    public Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        return inMemory.subList(0, 5);
    }

    @Override
    public long getAmountOfGamesOfAuthor(CsldUser author) {
        return 5;
    }

    @Override
    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        return 5;
    }

    @Override
    public boolean canEditGame(Game game) {
        return true;
    }

    @Override
    public Collection<Game> getGamesCommentedByUser(int userId) {
        return inMemory.subList(0, 5);
    }

    @Override
    public boolean isHidden(int gameId) {
        return false;
    }

    @Override
    public void toggleGameState(int gameId) {

    }

    @Override
    public Collection<Game> getGamesRatedByUser(int userId) {
        return inMemory.subList(0, 5);
    }

    @Override
    public List<Game> getMostPopularGames(int amountOfGames) {
        return inMemory.subList(0, 5);
    }

    @Override
    public ResourceReference getIconReference() {
        return new ResourceReference(Images.class, "Image") {
            @Override
            public IResource getResource() {
                return (IResource) attributes -> {
                };
            }
        };
    }
}
