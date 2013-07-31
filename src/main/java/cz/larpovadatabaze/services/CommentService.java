package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Comment;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:42
 */
public interface CommentService extends GenericService<Comment> {
    Comment getCommentOnGameFromUser(int userId, int gameId);

    void saveOrUpdate(Comment actualComment);
}
