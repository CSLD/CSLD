package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:42
 */
public interface Comments extends CRUDService<Comment, Integer> {
    Comment getCommentOnGameFromUser(int userId, int gameId);

    int getAmountOfComments();

    Collection<Comment> getLastComments(int amount);

    Collection<Comment> getLastComments(int first, int count);

    void hideComment(Comment comment);

    void unHideComment(Comment comment);

    Collection<Game> getGamesCommentedByUser(int userId);
}
