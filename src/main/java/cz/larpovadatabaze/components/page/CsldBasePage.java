package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.page.about.AboutDatabase;
import cz.larpovadatabaze.components.page.author.ListAuthor;
import cz.larpovadatabaze.components.page.game.ListGame;
import cz.larpovadatabaze.components.page.group.ListGroup;
import cz.larpovadatabaze.components.page.user.ListUser;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.AdminPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 * Base page from which all other pages are derived.
 */
public class CsldBasePage extends WebPage {
    public CsldBasePage(){
        if(!CsldAuthenticatedWebSession.get().isSignedIn()){
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();
            String[] data = strategy.load();
            if(data != null && data.length > 1){
                CsldAuthenticatedWebSession.get().signIn(data[0], data[1]);
            }
        }

        final Image mainLogo = new Image("mainLogo",
                new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getMainLogoPath()));
        add(mainLogo);

        final BookmarkablePageLink<CsldBasePage> homePageLink =
                new BookmarkablePageLink<CsldBasePage>("homePage", HomePage.class);
        final Image topMain = new Image("topMain",
                new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getTopLogoPath()));
        homePageLink.add(topMain);
        add(homePageLink);

        add(new LoginBoxPanel("login"));
        add(new LoggedBoxPanel("loggedInfo"));
        add(new AdminPanel("adminPanel"));

        add(new BookmarkablePageLink<CsldBasePage>("list-game", ListGame.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-authors", ListAuthor.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-users", ListUser.class));
        add(new BookmarkablePageLink<CsldBasePage>("list-groups", ListGroup.class));
        add(new BookmarkablePageLink<CsldBasePage>("about", AboutDatabase.class));

        add(new SearchBoxPanel("searchBox"));
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        //Protection against ClickJacking, prevents the page from being rendered in an iframe element
        response.setHeader("X-Frame-Options","deny");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery-ui-1.9.2.custom.js"));
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery.nivo.slider.pack.js"));
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery.nivo.slider.js"));

        response.render(CssHeaderItem.forUrl("/files/css/nivo-slider.css"));
        response.render(CssHeaderItem.forUrl("/files/css/style.css"));
        response.render(CssHeaderItem.forUrl("/files/css/smoothness/jquery-ui-1.8.24.custom.css"));
        super.renderHead(response);
    }
}
