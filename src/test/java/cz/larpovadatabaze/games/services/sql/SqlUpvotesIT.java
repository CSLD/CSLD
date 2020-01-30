package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Upvote;
import cz.larpovadatabaze.games.services.Upvotes;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class SqlUpvotesIT extends WithDatabase {
    private Upvotes underTest;

    @Before
    public void prepateUpvotes() {
        underTest = new SqlUpvotes(sessionFactory);
    }

    @Test
    public void returnProperlyExistingUpvotes() {
        Collection<Upvote> upvotes = underTest.forUserAndComment(masqueradeEntities.editor, masqueradeEntities.userComment);

        assertThat(upvotes, hasSize(1));
        assertThat(upvotes, hasItem(masqueradeEntities.editorUserComment));
    }

    @Test
    public void upvotingCommentAddsUpvoteForIt() {
        Transaction current = sessionFactory.getCurrentSession().beginTransaction();
        underTest.upvote(
                masqueradeEntities.administrator, masqueradeEntities.editorComment);
        current.commit();

        Collection<Upvote> upvotes =
                underTest.forUserAndComment(masqueradeEntities.administrator, masqueradeEntities.editorComment);
        assertThat(upvotes, hasSize(1));
    }

    @Test
    public void downVotingCommentRemovesExistingUpvote() {
        Transaction current = sessionFactory.getCurrentSession().beginTransaction();
        underTest.downvote(
                masqueradeEntities.editor, masqueradeEntities.userComment);
        current.commit();

        Collection<Upvote> upvotes =
                underTest.forUserAndComment(masqueradeEntities.administrator, masqueradeEntities.editorComment);
        assertThat(upvotes, hasSize(0));
    }
}
