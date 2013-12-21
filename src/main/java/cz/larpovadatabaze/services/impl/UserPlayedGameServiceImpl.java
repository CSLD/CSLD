package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.UserPlayedGameDAO;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class UserPlayedGameServiceImpl implements UserPlayedGameService {
    @Autowired
    private UserPlayedGameDAO userPlayedGameDAO;

    @Autowired
    private RatingService ratingService;

    @Override
    public List<UserPlayedGame> getAll() {
        return userPlayedGameDAO.findAll();
    }

    @Override
    public List<UserPlayedGame> getUnique(UserPlayedGame example) {
        return userPlayedGameDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(UserPlayedGame toRemove) {
        userPlayedGameDAO.makeTransient(toRemove);
    }

    @Override
    public List<UserPlayedGame> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public UserPlayedGame getUserPlayedGame(int gameId, int userId) {
        return userPlayedGameDAO.getUserPlayedGame(gameId, userId);
    }

    @Override
    public void saveOrUpdate(UserPlayedGame stateOfGame) {
        userPlayedGameDAO.saveOrUpdate(stateOfGame);

        if (stateOfGame.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.NONE)) {
            // Remove rating
            try {
                Rating rating = ratingService.getUserRatingOfGame(stateOfGame.getUserId(), stateOfGame.getGameId());
                if(rating != null) {
                    ratingService.remove(rating);
                }
            } catch (WrongParameterException e) {
                e.printStackTrace();
            }


        }
    }
}
