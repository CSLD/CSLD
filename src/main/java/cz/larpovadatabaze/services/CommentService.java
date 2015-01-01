package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;

import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:42
 */
public interface CommentService extends GenericService<Comment> {
    Comment getCommentOnGameFromUser(int userId, int gameId);

    void saveOrUpdate(Comment actualComment);

    List<Comment> getLastComments(int amount);

    int getAmountOfComments();

    List<Comment> getLastComments(long first, long count, Locale locale);

    void hideComment(Comment comment);

    void unHideComment(Comment comment);

    List<Game> getGamesCommentedByUser(int userId);

    long getAmountOfComments(Locale locale);
}
