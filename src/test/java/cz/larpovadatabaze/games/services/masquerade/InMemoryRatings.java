package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Ratings;

import java.util.List;
import java.util.stream.Collectors;

public class InMemoryRatings extends InMemoryCrud<Rating, Integer> implements Ratings {
    @Override
    public Rating getUserRatingOfGame(Integer userId, Integer gameId) {
        List<Rating> candidates = inMemory.stream()
                .filter(rating -> rating.getUser().getId().equals(userId) &&
                        rating.getGame().getId().equals(gameId))
                .collect(Collectors.toList());
        if (candidates.size() > 0) {
            return candidates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        return inMemory;
    }

    @Override
    public String getColor(Double rating) {
        return "average";
    }

    @Override
    public String getColorForGame(Game game) {
        return "average";
    }

    @Override
    public List<Rating> getRatingsOfGame(Game game) {
        return inMemory.stream()
                .filter(rating -> rating.getGame().equals(game))
                .collect(Collectors.toList());
    }

    @Override
    public void removeForUser(CsldUser toRemove) {

    }
}
