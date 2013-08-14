package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.admin.Administration;
import cz.larpovadatabaze.components.page.game.DeleteGamePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
        setVisibilityAllowed(((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser().getRole() > CsldRoles.USER.getRole());
    }
}
