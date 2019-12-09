package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.admin.AdministrationPage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * It is only link to the administration page.
 */
public class AdminPanel extends Panel {
    public AdminPanel(String id) {
        super(id);

        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("administration", AdministrationPage.class);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        boolean visible = false;
        if(loggedUser != null && loggedUser.getRole() > CsldRoles.USER.getRole()) {
            visible = true;
        }
        setVisibilityAllowed(visible);
    }
}
