package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class EditGamePanel extends Panel {
    public EditGamePanel(String id, Game toEdit) {
        super(id);

        PageParameters params = new PageParameters();
        params.add("id", toEdit.getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editGame", CreateOrUpdateGamePage.class, params);
        add(pageLink);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}
