package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.page.CreateOrUpdateGroupPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *  It contains link to the page for creating new Group.
 *  It is shown only to the logged user.
 */
public class AddGroupPanel extends Panel {
    public AddGroupPanel(String id) {
        super(id);

        BookmarkablePageLink<CsldBasePage> createGameIconLink =
                new BookmarkablePageLink<CsldBasePage>("createGroupIconLink", CreateOrUpdateGroupPage.class);
        add(createGameIconLink);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("createGroupLink", CreateOrUpdateGroupPage.class);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
