package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.UpvoteDAO;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Upvote;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Upvotes;
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
