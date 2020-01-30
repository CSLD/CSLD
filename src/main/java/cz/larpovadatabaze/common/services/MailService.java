package cz.larpovadatabaze.common.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.models.SelectedUser;

import java.util.List;

public interface MailService {
    /**
     * Send email to all the users who are interested in specific game.
     *
     * @param interestedUsers The Users that will be notified
     * @param nameOfTheGame   Name of the game
     * @param contentTemplate Text of the email to be send to the interested
     * @param subjectTemplate Subject of the email.
     */
    void sendInfoToInterestedUsers(List<SelectedUser> interestedUsers,
                                   String nameOfTheGame,
                                   String contentTemplate,
                                   String subjectTemplate);

    /**
     * Send information to the authors of the game
     *
     * @param currentGame     Selected game
     * @param contentTemplate Text of the email to be send to the interested
     * @param subjectTemplate Subject of the email.
     */
    void sendInfoToAuthors(Game currentGame,
                           String contentTemplate,
                           String subjectTemplate);

    /**
     * Send to the users who wants to play the game an information that a new event related to that game was added.
     *
     * @param interested The interested users
     * @param content    Content of the email.
     */
    void sendInfoAboutNewEventToAllInterested(List<CsldUser> interested,
                                              String subject,
                                              String content);

    void sendForgottenPassword(CsldUser recipient, String subject, String content);
}
