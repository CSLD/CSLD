package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.services.CRUDService;

import java.util.Collection;
import java.util.List;

/**
 * CRUD Service providing further methods to simplify work with the Comments.
 */
public interface Comments extends CRUDService<Comment, Integer> {
    /**
     * If a user commented specific game return the Comment. Otherwise return null.
     *
     * @param userId Id of the user
     * @param gameId Id of the game
     * @return Comment or null.
     */
    Comment getCommentOnGameFromUser(int userId, int gameId);

    /**
     * Return amount of comments in the table. Used for example for pagination.
     *
     * @return Amount of comments.
     */
    int getAmountOfComments();

    /**
     * Return most recent comments starting from 0. Shorthand for the full getLastComments method.
     *
     * @param amount How many comments to return
     * @return The most recent comments.
     */
    Collection<Comment> getLastComments(int amount);

    /**
     * Returns comments ordered by the time of adding. The count represents limit and first represents the offset for
     * the returned slice of the comments.
     *
     * @param first Index of the first returned comment
     * @param count Amount of comments to return starting inclusively from first.
     * @return Slice of comments within given bounds.
     */
    Collection<Comment> getLastComments(int first, int count);

    /**
     * Hide given comment. Normal users won't see it.
     *
     * @param comment Comment to hide.
     */
    void hideComment(Comment comment);

    /**
     * Make hidden comment visible. Normal users will see it again.
     *
     * @param comment Comment to make visible.
     */
    void unHideComment(Comment comment);

    /**
     * Return all comments that the current user has rights towards ordered by amount of upvotes
     * and then most recent.
     *
     * @return comments associated with given game
     */
    List<Comment> visibleForCurrentUserOrderedByUpvotes(Game game);

    /**
     * Remove all comments created by specific user.
     *
     * @param toRemove User whose comments should be removed.
     */
    void removeForUser(CsldUser toRemove);
}
