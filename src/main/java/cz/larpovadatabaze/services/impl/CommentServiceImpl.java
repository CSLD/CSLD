package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.CommentDAO;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 21:43
 */
@Repository
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Override
    public List<Comment> getAll() {
        return commentDAO.findAll();
    }

    @Override
    public List<Comment> getUnique(Comment example) {
        List<Comment> uniqueResult = commentDAO.findByExample(example, new String[]{});
        commentDAO.flush();
        return uniqueResult;
    }

    @Override
    public void remove(Comment toRemove) {
        commentDAO.makeTransient(toRemove);
    }

    @Override
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        return commentDAO.getCommentOnGameFromUser(userId, gameId);
    }

    @Override
    public void saveOrUpdate(Comment actualComment) {
        commentDAO.saveOrUpdate(actualComment);
    }

    @Override
    public List<Comment> getLastComments(int amount) {
        return commentDAO.getLastComments(amount);
    }

    @Override
    public List<Comment> getLastComments(long first, long count) {
        return commentDAO.getLastComments(((Long)first).intValue(), ((Long)count).intValue());
    }

    @Override
    public int getAmountOfComments() {
        return commentDAO.getAmountOfComments();
    }
}
