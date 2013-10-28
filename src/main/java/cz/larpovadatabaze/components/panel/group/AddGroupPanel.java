package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *  It contains link to the page for creating new Group.
 *  It is shown only to the logged user.
 */
public class AddGroupPanel extends Panel {
    public AddGroupPanel(String id) {
        super(id);

        Image createGameIcon = new Image("createGroupIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getPlusIconPath()));
        BookmarkablePageLink<CsldBasePage> createGameIconLink =
                new BookmarkablePageLink<CsldBasePage>("createGroupIconLink", CreateOrUpdateGroupPage.class);
        createGameIconLink.add(createGameIcon);
        add(createGameIconLink);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("createGroupLink", CreateOrUpdateGroupPage.class);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
