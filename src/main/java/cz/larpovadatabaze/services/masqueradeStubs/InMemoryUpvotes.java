package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Upvote;
import cz.larpovadatabaze.services.Upvotes;

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
