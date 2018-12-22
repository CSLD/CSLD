package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Upvote;

import java.util.Collection;

/**
 * Handle the Upvote.
 */
public interface UpvoteService extends GenericService<Upvote> {
    /**
     * UpVotes the comment.
     * @param comment Comment to be upvoted
     * @param user User doing the upvoting
     */
    void upvote(CsldUser user, Comment comment);

    /**
     * DownVotes the comment. It is possible only to remove the upvote. There is no way to get into the minus.
     * @param comment Comment to be downvoted
     * @param user User doing the downvoting
     */
    void downvote(CsldUser user, Comment comment);

    /**
     * Return the upvotes for given User and Comment. It should basically be just one.
     * @param user Existing user
     * @param comment Existing Comment
     * @return All the Upvotes given by the specific user to the Comment
     */
    Collection<Upvote> forUserAndComment(CsldUser user, Comment comment);
}
