package cz.larpovadatabaze.components.panel;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.CsldSignInPage;
import cz.larpovadatabaze.components.page.user.RegisterUserPage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 13:09
 */
public class LoginBoxPanel extends Panel {
    public LoginBoxPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<CsldBasePage>("register", RegisterUserPage.class));
        add(new BookmarkablePageLink<CsldBasePage>("signIn", CsldSignInPage.class));
    }

    protected void onConfigure() {
        setVisibilityAllowed(!CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
