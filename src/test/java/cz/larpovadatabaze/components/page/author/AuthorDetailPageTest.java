package cz.larpovadatabaze.components.page.author;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.components.panel.game.CommentsListPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.user.PersonDetailPanel;
import cz.larpovadatabaze.components.panel.user.RatingsListPanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.junit.Test;

/**
 *
 */
public class AuthorDetailPageTest extends AcceptanceTest {
    @Test
    public void runAsGuest() {
        tester.startPage(UserDetailPage.class, UserDetailPage.paramsForUser(masqueradeBuilder.getUser()));
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
        TestUtils.logUser(masqueradeBuilder.getUser());

        tester.startPage(UserDetailPage.class, UserDetailPage.paramsForUser(masqueradeBuilder.getUser()));
        tester.assertRenderedPage(UserDetailPage.class);

        tester.assertComponent("updateUserLink", BookmarkablePageLink.class);
        tester.assertVisible("updateUserLink");
    }
}
