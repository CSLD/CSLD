package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.admin.Administration;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 */
public class AdminPanel extends Panel {
    public AdminPanel(String id) {
        super(id);

        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("administration", Administration.class);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        CsldUser loggedUser = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        boolean visible = false;
        if(loggedUser != null && loggedUser.getRole() > CsldRoles.USER.getRole()) {
            visible = true;
        }
        setVisibilityAllowed(visible);
    }
}
