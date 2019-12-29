package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.games.components.page.ListGamePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 9:43
 */
public class SignOutPage extends CsldBasePage {
    public SignOutPage(final PageParameters parameters)
    {
        CsldAuthenticatedWebSession.get().signOut();
        IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                .getAuthenticationStrategy();
        strategy.remove();

        final BookmarkablePageLink<CsldUser> homePageLink =
                new BookmarkablePageLink<CsldUser>("homePageLink", ListGamePage.class);
        add(homePageLink);
    }
}
