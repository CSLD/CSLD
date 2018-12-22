package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.UpvoteDAO;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Upvote;
import cz.larpovadatabaze.services.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class SqlUpvoteService implements UpvoteService {
    @Autowired
    UpvoteDAO sqlUpVotes;

    @Override
    public void upvote(CsldUser user, Comment comment) {
        sqlUpVotes.upvote(user, comment);
    }

    @Override
    public void downvote(CsldUser user, Comment comment) {
        sqlUpVotes.downvote(user, comment);
    }

    @Override
    public Collection<Upvote> forUserAndComment(CsldUser user, Comment comment) {
        Upvote upvote = new Upvote();
        upvote.setComment(comment);
        upvote.setUser(user);

        return sqlUpVotes.findByExample(upvote);
    }

    @Override
    public List<Upvote> getAll() {
        return sqlUpVotes.findAll();
    }

    @Override
    public List<Upvote> getUnique(Upvote example) {
        return sqlUpVotes.findByExample(example);
    }

    @Override
    public void remove(Upvote toRemove) {
        sqlUpVotes.makeTransient(toRemove);
    }

    @Override
    public List<Upvote> getFirstChoices(String startsWith, int maxChoices) {
        return null;
    }
}
