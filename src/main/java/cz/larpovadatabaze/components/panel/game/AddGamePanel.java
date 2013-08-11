package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 */
public class AddGamePanel extends Panel {
    public AddGamePanel(String id) {
        super(id);

        Image createGameIcon = new Image("addGameIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getPlusIconPath()));
        BookmarkablePageLink<CsldBasePage> createGameIconLink =
                new BookmarkablePageLink<CsldBasePage>("addGameIconLink", CreateOrUpdateGamePage.class);
        createGameIconLink.add(createGameIcon);
        add(createGameIconLink);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("addGameLink", CreateOrUpdateGamePage.class);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
