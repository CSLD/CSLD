package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.entities.UserPlayedGame;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
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
    public double getAverageRating() {
        return 5.5;
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        return inMemory;
    }

    @Override
    public void delete(Rating rating) {

    }

    @Override
    public UserPlayedGame getUserPlayedGame(int gameId, int userId) {
        return null;
    }

    @Override
    public boolean saveOrUpdate(UserPlayedGame stateOfGame) {
        return false;
    }
}
