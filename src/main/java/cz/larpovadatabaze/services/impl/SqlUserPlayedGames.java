package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.UserPlayedGameDAO;
import cz.larpovadatabaze.donations.service.BankAccount;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Information about the status of the user towards specific game. The user either want to play game or played game
 * or neither.
 */
@Repository
@Transactional
public class SqlUserPlayedGames implements UserPlayedGameService {
    private final static Logger logger = Logger.getLogger(BankAccount.class);

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
                Rating rating = ratingService.getUserRatingOfGame(stateOfGame.getPlayerOfGame().getId(), stateOfGame.getGame().getId());
                if(rating != null) {
                    ratingService.remove(rating);
                }
            } catch (WrongParameterException e) {
                logger.error(e);
            }
        }
    }
}
