package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.components.page.user.CreateOrUpdateUserPage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 */
public class AddAuthorPanel extends Panel {
    public AddAuthorPanel(String id) {
        super(id);

        Image createAuthorImage = new Image("createAuthorImage", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getPlusIconPath()));
        BookmarkablePageLink<CsldBasePage> createAuthor =
                new BookmarkablePageLink<CsldBasePage>("createAuthorIcon", CreateOrUpdateUserPage.class);
        createAuthor.add(createAuthorImage);
        add(createAuthor);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("createAuthor", CreateOrUpdateUserPage.class);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
