package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.dao.UpvoteDAO;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Upvote;
import cz.larpovadatabaze.services.Upvotes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional
public class SqlUpvotes extends CRUD<Upvote, Integer> implements Upvotes {
    private UpvoteDAO sqlUpVotes;

    @Autowired
    public SqlUpvotes(UpvoteDAO sqlUpVotes) {
        super(sqlUpVotes);
        this.sqlUpVotes = sqlUpVotes;
    }

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
}
