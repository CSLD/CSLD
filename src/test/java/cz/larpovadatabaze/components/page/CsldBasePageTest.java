package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.TestUtils;
import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.page.about.AboutDatabasePage;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.components.panel.home.AdvertisementPanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.AdminPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.services.CsldUsers;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test functionality specific to the BasePage and therefore to all pages.
 * The functionality is tested using HomePage as an example.
 */
public class CsldBasePageTest extends WithWicket {
    @Autowired
    CsldUsers users;

    @Test
    public void homePageIsRenderedForGuest() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);

        tester.assertLabel("pageTitle", "Česko-slovenská larpová databáze");
        tester.assertComponent("list-game", BookmarkablePageLink.class);
        tester.assertComponent("about", BookmarkablePageLink.class);

        tester.assertComponent("user", LoginBoxPanel.class);

        tester.assertComponent("searchBox", SearchBoxPanel.class);
        tester.assertComponent("advertisements", AdvertisementPanel.class);
    }

    @Test
    public void aboutLeadsToAbout() {
        tester.startPage(HomePage.class);

        tester.clickLink("about");
        tester.assertRenderedPage(AboutDatabasePage.class);
    }

    @Test
    public void listGameLeadsToListOfGames() {
        tester.startPage(HomePage.class);

        tester.clickLink("list-game");
        tester.assertRenderedPage(ListGamePage.class);
    }

    @Test
    public void homePageIsRenderedForUser() {
        TestUtils.logUser(users.getById(3));

        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);

        tester.assertComponent("user", LoggedBoxPanel.class);
        tester.assertInvisible("adminPanel");
    }

    @Test
    public void homePageIsRenderedForEditor() {
        TestUtils.logUser(users.getById(2));

        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);

        tester.assertComponent("adminPanel", AdminPanel.class);
        tester.assertVisible("adminPanel");
    }
}
