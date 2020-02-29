package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.games.components.panel.AbstractListCommentPanel;
import cz.larpovadatabaze.games.components.panel.GameListPanel;
import cz.larpovadatabaze.games.components.panel.ListGamesWithAnnotations;
import cz.larpovadatabaze.users.components.panel.PersonDetailPanel;
import cz.larpovadatabaze.users.components.panel.RatingsListPanel;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class AuthorDetailPageTest extends WithWicket {
    private CsldUser visualized;
    private UserDetailPage underTest;
    @Autowired
    private CsldUsers users;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        visualized = users.getById(3);
        underTest = new UserDetailPage(UserDetailPage.paramsForUser(visualized));
    }

    @Test
    public void runAsGuest() {
        tester.startPage(underTest);
        tester.assertRenderedPage(UserDetailPage.class);

        tester.assertComponent("personDetail", PersonDetailPanel.class);
        tester.assertComponent("comments", AbstractListCommentPanel.class);
        tester.assertComponent("annotatedGamesOfAuthor", ListGamesWithAnnotations.class);
        tester.assertComponent("wantedGamesPanel", GameListPanel.class);

        tester.assertComponent("ratedGames", RatingsListPanel.class);
        tester.assertVisible("ratedGames");

        tester.assertInvisible("updateUserLink");
    }

    @Test
    public void runAsLoggedUser() {
        logUser(visualized);

        tester.startPage(underTest);
        tester.assertRenderedPage(UserDetailPage.class);

        tester.assertComponent("updateUserLink", BookmarkablePageLink.class);
        tester.assertVisible("updateUserLink");
    }
}
