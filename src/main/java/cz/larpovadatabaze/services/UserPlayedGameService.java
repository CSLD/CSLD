package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.UserPlayedGame;

/**
 *
 */
public interface UserPlayedGameService extends GenericService<UserPlayedGame> {

    UserPlayedGame getUserPlayedGame(int gameId, int userId);

    void saveOrUpdate(UserPlayedGame stateOfGame);
}
