package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.page.about.AboutDatabase;
import cz.larpovadatabaze.components.page.author.ListAuthor;
import cz.larpovadatabaze.components.page.game.ListGame;
import cz.larpovadatabaze.components.page.group.ListGroup;
import cz.larpovadatabaze.components.page.user.ListUser;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoggedBoxPanel;
import cz.larpovadatabaze.components.panel.user.LoginBoxPanel;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.http.WebResponse;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 10:18
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

        add(new LoginBoxPanel("login"));
        add(new LoggedBoxPanel("loggedInfo"));

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
        response.render(CssHeaderItem.forUrl("/files/css/style.css"));
        response.render(CssHeaderItem.forUrl("/files/css/nivo-slider.css"));
        response.render(CssHeaderItem.forUrl("/files/css/smoothness/jquery-ui-1.8.24.custom.css"));
        super.renderHead(response);
    }
}
