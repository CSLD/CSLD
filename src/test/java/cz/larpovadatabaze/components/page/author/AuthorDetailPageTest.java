package cz.larpovadatabaze.components.page.author;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.components.panel.game.CommentsListPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.user.PersonDetailPanel;
import cz.larpovadatabaze.components.panel.user.RatingsListPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUsers;
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
        tester.assertComponent("comments", CommentsListPanel.class);
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
