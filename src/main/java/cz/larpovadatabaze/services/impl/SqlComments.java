package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.CommentDAO;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class SqlComments implements CommentService {
    private static final Logger logger = Logger.getLogger(SqlComments.class);

    private CommentDAO commentDAO;
    private GameService gameService;

    @Autowired
    public SqlComments(CommentDAO commentDAO, GameService gameService) {
        this.commentDAO = commentDAO;
        this.gameService = gameService;
    }

    @Override
    public List<Comment> getAll() {
        return commentDAO.findAll();
    }

    @Override
    public Comment getCommentOnGameFromUser(int userId, int gameId) {
        return commentDAO.getCommentOnGameFromUser(userId, gameId);
    }

    @Override
    public int getAmountOfComments() {
        return commentDAO.getAmountOfComments();
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
    public List<Comment> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("Comments does not support autocompletion");
    }

    @Override
    public void saveOrUpdate(Comment actualComment) {
        commentDAO.saveOrUpdate(actualComment);

        gameService.evictGame(actualComment.getGame().getId());
    }

    @Override
    public Collection<Comment> getLastComments(int amount) {
        return new LinkedHashSet<>(commentDAO.getLastComments(amount));
    }

    @Override
    public Collection<Comment> getLastComments(long first, long count) {
        return new LinkedHashSet<>(commentDAO.getLastComments(((Long)first).intValue(), ((Long)count).intValue()));
    }

    @Override
    public void hideComment(Comment comment) {
        if (Boolean.TRUE.equals(comment.getHidden())) return; // Nothing to do

        comment.setHidden(true);
        commentDAO.saveOrUpdate(comment);

        // Log
        logger.info("Editor #"+ UserUtils.getLoggedUser().getId()+" hidden comment of user #"+comment.getUser().getId()+" for game #"+comment.getGame().getId());
    }

    @Override
    public void unHideComment(Comment comment) {
        if (Boolean.FALSE.equals(comment.getHidden())) return; // Nothing to do

        comment.setHidden(false);
        commentDAO.saveOrUpdate(comment);

        // Log
        logger.info("Editor #"+ UserUtils.getLoggedUser().getId()+" unhidden comment of user #"+comment.getUser().getId()+" for game #"+comment.getGame().getId());
    }

    @Override
    public Collection<Game> getGamesCommentedByUser(int userId) {
        return gameService.getGamesCommentedByUser(userId);
    }


}
