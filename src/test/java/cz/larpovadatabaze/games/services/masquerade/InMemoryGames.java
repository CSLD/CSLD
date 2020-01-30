package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
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
    public List<Game> getLastGames(int limit) {
        return inMemory.subList(0, 5);
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
    public List<Game> getMostPopularGames(int limit) {
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
