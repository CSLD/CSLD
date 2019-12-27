package cz.larpovadatabaze.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.services.AppUsers;
import cz.larpovadatabaze.services.Comments;
import cz.larpovadatabaze.services.Games;
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
    public void setUp() {
        super.setUp();

        mockGames = Mockito.mock(Games.class);
        mockAppUsers = Mockito.mock(AppUsers.class);
        underTest = new SqlComments(sessionFactory, mockGames, mockAppUsers);
    }

    @Test
    public void getExistingCommentOnGameForUser() {
        Comment result = underTest.getCommentOnGameFromUser(
                masqueradeBuilder.getEditor().getId(), masqueradeBuilder.getSecondMasquerade().getId());

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
        int editorCommentId = masqueradeBuilder.getEditorComment().getId();
        underTest.hideComment(masqueradeBuilder.getEditorComment());

        sessionFactory.getCurrentSession().evict(masqueradeBuilder.getEditorComment());
        Comment hidden = underTest.getById(editorCommentId);
        assertThat(hidden.getHidden(), is(true));

        underTest.unHideComment(hidden);
        assertThat(sessionFactory.getCurrentSession().get(Comment.class, 2).getHidden(), is(false));
    }
}
