package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;

import java.util.List;

public interface GamesWithState {
    /**
     * Return games that were played by user.
     *
     * @param user User who played the games
     * @return List of the games played by the person.
     */
    List<IGameWithRating> getPlayedByUser(CsldUser user);

    /**
     * Return games that the user wants to play
     *
     * @param user User who wants to play the games
     * @return List of the games that the user wants to play.
     */
    List<Game> getWantedByUser(CsldUser user);

    /**
     * Return amount of games played by user.
     *
     * @param user User who wants to play the games.
     * @return Amount of the games the user wants to play.
     */
    long getAmountOfGamesPlayedBy(CsldUser user);

    /**
     * Send email with the information that new event was added to the game.
     *
     * @param game Game for which the event was added.
     * @param url  Url of the game.
     */
    void sendEmailToInterested(Game game, String url);
}
