package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.CreateOrUpdateGroupPage;
import cz.larpovadatabaze.components.page.group.ManageGroupPage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 */
public class AddAuthorsToGroupPanel extends Panel {
    public AddAuthorsToGroupPanel(String id, CsldGroup group) {
        super(id);

        PageParameters params = new PageParameters();
        params.add("id",group.getId());

        Image createGameIcon = new Image("addAuthorsIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getPlusIconPath()));
        BookmarkablePageLink<CsldBasePage> createGameIconLink =
                new BookmarkablePageLink<CsldBasePage>("addAuthorsIconLink", ManageGroupPage.class, params);
        createGameIconLink.add(createGameIcon);
        add(createGameIconLink);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("addAuthorsLink", ManageGroupPage.class, params);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
