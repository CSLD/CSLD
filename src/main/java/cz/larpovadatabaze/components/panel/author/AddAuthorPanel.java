package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.author.CreateOrUpdateAuthorPage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * It shows link to page for creating authors. Link is combined form image and text.
 * This panel is shown only for logged users.
 */
public class AddAuthorPanel extends Panel {
    public AddAuthorPanel(String id) {
        super(id);

        BookmarkablePageLink<CsldBasePage> createAuthor =
                new BookmarkablePageLink<CsldBasePage>("createAuthorIcon", CreateOrUpdateAuthorPage.class);
        add(createAuthor);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("createAuthor", CreateOrUpdateAuthorPage.class);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
