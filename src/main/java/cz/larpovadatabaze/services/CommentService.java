package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:42
 */
public interface CommentService extends GenericService<Comment> {
    void saveOrUpdate(Comment actualComment);

    Collection<Comment> getLastComments(int amount);

    Collection<Comment> getLastComments(long first, long count, Locale locale);

    void hideComment(Comment comment);

    void unHideComment(Comment comment);

    Collection<Game> getGamesCommentedByUser(int userId);
}
