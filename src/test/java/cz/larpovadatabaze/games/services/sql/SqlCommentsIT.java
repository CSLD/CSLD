package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SqlCommentsIT extends WithDatabase {
    private Comments underTest;
    private Games mockGames;
    private AppUsers mockAppUsers;

    @Before()
    public void prepareClassUnderTest() {
        mockGames = Mockito.mock(Games.class);
        mockAppUsers = Mockito.mock(AppUsers.class);
        underTest = new SqlComments(sessionFactory, mockGames, mockAppUsers);
    }

    @Test
    public void getExistingCommentOnGameForUser() {
        Comment result = underTest.getCommentOnGameFromUser(
                masqueradeEntities.editor.getId(), masqueradeEntities.secondMasquerade.getId());

        assertThat(result.getComment(), is("There were some flwas but overally likeable game."));
    }

    @Test
    public void returnRealAmountOfComments() {
        int result = underTest.getAmountOfComments();

        assertThat(result, is(3));
    }

    @Test
    public void returnLastCommentsFromZero() {
        Collection<Comment> result = underTest.getLastComments(1);

        assertThat(result.size(), is(1));

        Comment mostRecent = result.iterator().next();
        assertThat(mostRecent.getComment(), is("My first LARP and it was so freaking awesome."));
    }

    @Test
    public void returnCommentsIgnoringTheMostRecent() {
        Collection<Comment> result = underTest.getLastComments(1, 1);

        assertThat(result.size(), is(1));

        Comment mostRecent = result.iterator().next();
        assertThat(mostRecent.getComment(), is("There were some flwas but overally likeable game."));
    }

    @Test
    public void hidingOfCommentsProperlyChangeState() {
        Transaction current = sessionFactory.getCurrentSession().beginTransaction();
        int editorCommentId = masqueradeEntities.editorComment.getId();
        underTest.hideComment(masqueradeEntities.editorComment);
        current.commit();

        sessionFactory.getCurrentSession().evict(masqueradeEntities.editorComment);

        Comment hidden = underTest.getById(editorCommentId);
        assertThat(hidden.getHidden(), is(true));

        current = sessionFactory.getCurrentSession().beginTransaction();
        underTest.unHideComment(hidden);
        current.commit();

        Comment shown = underTest.getById(editorCommentId);
        assertThat(shown.getHidden(), is(false));
    }
}
