package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Upvote;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.services.Upvotes;

import java.util.Collection;

public class InMemoryUpvotes extends InMemoryCrud<Upvote, Integer> implements Upvotes {
    @Override
    public void upvote(CsldUser user, Comment comment) {
        saveOrUpdate(new Upvote(user, comment));
    }

    @Override
    public void downvote(CsldUser user, Comment comment) {

    }

    @Override
    public Collection<Upvote> forUserAndComment(CsldUser user, Comment comment) {
        return null;
    }
}
