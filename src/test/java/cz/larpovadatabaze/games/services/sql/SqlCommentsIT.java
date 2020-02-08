package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.models.Page;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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

        assertThat(result, is(4));
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

    @Test
    public void returnVisibleForGameAndStandardUser() {
        when(mockAppUsers.isAtLeastEditor()).thenReturn(false);

        List<Comment> visibleOrderedComments =
                underTest.visibleForCurrentUserOrderedByUpvotes(masqueradeEntities.secondMasquerade, new Page(4, 0));

        assertThat(visibleOrderedComments, hasSize(2));
        assertThat(visibleOrderedComments, contains(
                masqueradeEntities.userComment,
                masqueradeEntities.editorComment));
    }

    @Test
    public void returnVisibleForGameAndEditor() {
        when(mockAppUsers.isAtLeastEditor()).thenReturn(true);
        when(mockAppUsers.getLoggedUserId()).thenReturn(-1);

        List<Comment> visibleOrderedComments =
                underTest.visibleForCurrentUserOrderedByUpvotes(masqueradeEntities.secondMasquerade, new Page(4, 0));

        assertThat(visibleOrderedComments, hasSize(3));
        assertThat(visibleOrderedComments, contains(
                masqueradeEntities.userComment,
                masqueradeEntities.editorComment,
                masqueradeEntities.administratorComment));
    }
}
